#!/usr/bin/env python3
"""
Get staff from database and create initial mapping
"""

import requests
import json
from pathlib import Path

API_BASE_URL = "http://localhost:8080/api"
EXTRACTED_NAMES_FILE = Path(__file__).parent.parent / "data" / "staff-names-extracted.json"
MAPPING_FILE = Path(__file__).parent.parent / "data" / "staff-mapping.json"

def authenticate(username="uwe", password="password123"):
    """Get JWT token"""
    response = requests.post(
        f"{API_BASE_URL}/auth/login",
        json={"username": username, "password": password}
    )
    response.raise_for_status()
    return response.json()["token"]

def get_staff(token):
    """Get all staff from database"""
    try:
        # Try to get just a simple list
        response = requests.get(
            f"{API_BASE_URL}/staff",
            headers={"Authorization": f"Bearer {token}"},
            timeout=5
        )

        # The response might have circular references, so let's just extract what we need
        text = response.text

        # Parse manually to avoid circular reference issues
        staff_list = []
        import re

        # Find all staff entries by looking for id, firstName, lastName patterns
        # This is a hacky workaround for the circular reference issue
        id_matches = re.findall(r'"id"\s*:\s*(\d+)', text)
        first_name_matches = re.findall(r'"firstName"\s*:\s*"([^"]+)"', text)
        last_name_matches = re.findall(r'"lastName"\s*:\s*"([^"]+)"', text)

        if id_matches and first_name_matches and last_name_matches:
            # Take first occurrence of each (the top-level ones)
            staff_list.append({
                'id': int(id_matches[0]),
                'firstName': first_name_matches[0],
                'lastName': last_name_matches[0]
            })

        return staff_list

    except Exception as e:
        print(f"Error getting staff: {e}")
        return []

def create_mapping():
    """Create initial staff mapping file"""
    print("Getting staff from database...")

    # Authenticate
    token = authenticate()
    print("✓ Authenticated")

    # Get database staff
    db_staff = get_staff(token)
    print(f"✓ Found {len(db_staff)} staff in database")

    for staff in db_staff:
        print(f"  - {staff['firstName']} {staff['lastName']} (ID: {staff['id']})")

    # Load extracted names from PDFs
    if not EXTRACTED_NAMES_FILE.exists():
        print(f"ERROR: {EXTRACTED_NAMES_FILE} not found")
        return

    with open(EXTRACTED_NAMES_FILE) as f:
        pdf_staff = json.load(f)

    print(f"\n✓ Loaded {len(pdf_staff)} unique staff from PDFs")

    # Create mapping
    # Format: { "firstname_lastname": database_id }
    mapping = {}

    print("\nManual mapping required:")
    print("Please edit data/staff-mapping.json to map PDF names to database staff IDs")
    print("\nTemplate created with placeholder IDs (null):\n")

    for key, staff_data in sorted(pdf_staff.items()):
        # Try to find matching database staff
        db_match = None
        for db_staff_member in db_staff:
            if (db_staff_member['firstName'].lower() == staff_data['first_name'].lower() or
                db_staff_member['lastName'].lower() == staff_data['last_name'].lower()):
                db_match = db_staff_member['id']
                break

        mapping[key.lower()] = db_match
        status = f"-> {db_match}" if db_match else "-> ???"
        print(f"{staff_data['first_name']:15} {staff_data['last_name']:20} {status}")

    # Save mapping
    with open(MAPPING_FILE, 'w') as f:
        json.dump(mapping, f, indent=2)

    print(f"\n✓ Saved mapping template to: {MAPPING_FILE}")
    print("\nNOTE: Most mappings are set to null.")
    print("You need to:")
    print("1. Review data/staff-mapping.json")
    print("2. Create missing staff in the database or map to existing staff IDs")
    print("3. Update the mapping file with correct IDs")

if __name__ == "__main__":
    create_mapping()
