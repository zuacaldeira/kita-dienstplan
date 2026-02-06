#!/usr/bin/env python3
"""
Generate staff mapping from database after migration
"""

import requests
import json
from pathlib import Path
import sys

API_BASE_URL = "http://localhost:8080/api"
MAPPING_FILE = Path(__file__).parent.parent / "data" / "staff-mapping.json"

# Mapping of PDF names to full names in database
PDF_NAME_TO_DB_NAME = {
    "omar_alaoui": "Omar Alaoui",
    "isabel_sovic": "Isabel Sovic",
    "prudence_noubissie deutcheu": "Prudence Noubissie Deutcheu",
    "bertheline_kock nkoue epse tiwe": "Bertheline Kock Nkoue Epse Tiwe",
    "eunice ellen_silva": "Eunice Ellen Silva",
    "rick_otto": "Rick Otto",
    "marion_dehnel": "Marion Dehnel",
    "elisa_de sá zua caldeira": "Elisa de Sá Zua Caldeira",
    "camilla_mannshardt oliveira": "Camilla Mannshardt Oliveira",
    "alexandre_zua caldeira": "Alexandre Zua Caldeira",
    "violetta_hristozova": "Violetta Hristozova",
    "violeta_hristozova": "Violetta Hristozova",  # Spelling variation
    "hendrixa_kogningbo": "Hendrixa Kogningbo",
    "fritz_sievers": "Fritz Sievers",
    "yu_lou": "Yu Lou",
    "letícia_viana": "Letícia Viana",
    "letitia_viana": "Letícia Viana",  # Spelling variation
    "camilla_weber": "Camilla Weber",
    "joão pedro_amaral ferreira": "João Pedro Amaral Ferreira",
    "joão pedro_amoral ferreira": "João Pedro Amaral Ferreira",  # Typo in some PDFs
    "iara_ferreira": "Iara Ferreira",
    "karin_harboe": "Karin Harboe"
}

def authenticate(username="uwe", password="password123"):
    """Get JWT token"""
    try:
        response = requests.post(
            f"{API_BASE_URL}/auth/login",
            json={"username": username, "password": password}
        )
        response.raise_for_status()
        return response.json()["token"]
    except Exception as e:
        print(f"❌ Authentication failed: {e}")
        sys.exit(1)

def get_staff_via_mysql():
    """Get staff directly from MySQL database"""
    import subprocess

    # Read database password from application.properties
    props_file = Path(__file__).parent.parent / "backend" / "src" / "main" / "resources" / "application.properties"

    with open(props_file) as f:
        for line in f:
            if line.startswith("spring.datasource.password="):
                db_password = line.split("=", 1)[1].strip()
                break

    # Query database
    cmd = [
        "mysql",
        "-u", "kita_admin",
        f"-p{db_password}",
        "kita_casa_azul",
        "-e", "SELECT id, first_name, last_name, full_name FROM staff ORDER BY id;",
        "--batch",
        "--skip-column-names"
    ]

    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=True)
        staff_list = []

        for line in result.stdout.strip().split('\n'):
            if line:
                parts = line.split('\t')
                if len(parts) >= 4:
                    staff_list.append({
                        'id': int(parts[0]),
                        'firstName': parts[1],
                        'lastName': parts[2],
                        'fullName': parts[3]
                    })

        return staff_list
    except Exception as e:
        print(f"❌ Database query failed: {e}")
        return []

def generate_mapping():
    """Generate staff mapping file"""
    print("Generating staff mapping from database...\n")

    # Get staff from database
    staff_list = get_staff_via_mysql()

    if not staff_list:
        print("❌ No staff found in database")
        print("   Make sure the V3 migration has run successfully")
        sys.exit(1)

    print(f"✓ Found {len(staff_list)} staff members in database\n")

    # Create mapping
    mapping = {}

    for pdf_key, db_name in PDF_NAME_TO_DB_NAME.items():
        # Find matching staff
        staff_id = None
        for staff in staff_list:
            if staff['fullName'] == db_name:
                staff_id = staff['id']
                break

        if staff_id:
            mapping[pdf_key] = staff_id
            print(f"✓ {pdf_key:40} → ID {staff_id:3} ({db_name})")
        else:
            mapping[pdf_key] = None
            print(f"⚠ {pdf_key:40} → NOT FOUND in database")

    # Save mapping
    with open(MAPPING_FILE, 'w') as f:
        json.dump(mapping, f, indent=2)

    print(f"\n✓ Saved mapping to: {MAPPING_FILE}")

    # Check for unmapped
    unmapped = [k for k, v in mapping.items() if v is None]
    if unmapped:
        print(f"\n⚠ Warning: {len(unmapped)} staff not found in database:")
        for key in unmapped:
            print(f"   - {key} (looking for: {PDF_NAME_TO_DB_NAME[key]})")
    else:
        print("\n✅ All staff mapped successfully!")

    return True

if __name__ == "__main__":
    generate_mapping()
