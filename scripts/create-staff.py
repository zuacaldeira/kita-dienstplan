#!/usr/bin/env python3
"""
Create staff members in database from extracted PDF names
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

def get_age_groups(token):
    """Get all age groups"""
    response = requests.get(
        f"{API_BASE_URL}/age-groups",
        headers={"Authorization": f"Bearer {token}"}
    )
    if response.status_code == 200:
        return response.json()
    return []

def create_staff_member(token, staff_data, group_id):
    """Create a single staff member"""
    try:
        response = requests.post(
            f"{API_BASE_URL}/staff",
            json={
                "firstName": staff_data['first_name'],
                "lastName": staff_data['last_name'],
                "role": staff_data['roles'][0] if staff_data['roles'] else "Erzieher",
                "email": f"{staff_data['first_name'].lower()}.{staff_data['last_name'].lower()}@kitacasaazul.de",
                "phone": "+49 XXX XXXXXXX",
                "groupId": group_id,
                "isActive": True
            },
            headers={
                "Authorization": f"Bearer {token}",
                "Content-Type": "application/json"
            }
        )

        if response.status_code == 201:
            data = response.json()
            return data['id']
        else:
            print(f"  ✗ Failed to create {staff_data['first_name']} {staff_data['last_name']}: {response.status_code}")
            if response.text:
                print(f"    Response: {response.text[:200]}")
            return None

    except Exception as e:
        print(f"  ✗ Error creating {staff_data['first_name']} {staff_data['last_name']}: {e}")
        return None

def main():
    print("Creating staff members in database...")

    # Authenticate
    token = authenticate()
    print("✓ Authenticated\n")

    # Get age groups
    age_groups = get_age_groups(token)
    if not age_groups:
        print("ERROR: No age groups found in database")
        print("Please create age groups first")
        return

    print(f"✓ Found {len(age_groups)} age groups")
    for group in age_groups:
        print(f"  - {group['name']} (ID: {group['id']})")

    # Use first age group as default
    default_group_id = age_groups[0]['id']
    print(f"\nUsing default group ID: {default_group_id}\n")

    # Load extracted names from PDFs
    if not EXTRACTED_NAMES_FILE.exists():
        print(f"ERROR: {EXTRACTED_NAMES_FILE} not found")
        return

    with open(EXTRACTED_NAMES_FILE) as f:
        pdf_staff = json.load(f)

    print(f"✓ Loaded {len(pdf_staff)} unique staff from PDFs\n")

    # Create staff members
    mapping = {}
    created = 0
    failed = 0

    for key, staff_data in sorted(pdf_staff.items()):
        # Skip internships and generic roles
        if any(skip in staff_data['roles'][0] for skip in ['Praktikant', 'Praktikantin']):
            print(f"⊘ Skipping intern: {staff_data['first_name']} {staff_data['last_name']}")
            continue

        print(f"Creating: {staff_data['first_name']} {staff_data['last_name']} ({staff_data['roles'][0]})")

        staff_id = create_staff_member(token, staff_data, default_group_id)

        if staff_id:
            mapping[key.lower()] = staff_id
            created += 1
            print(f"  ✓ Created with ID: {staff_id}")
        else:
            mapping[key.lower()] = None
            failed += 1

    # Save mapping
    with open(MAPPING_FILE, 'w') as f:
        json.dump(mapping, f, indent=2)

    print(f"\n{'='*60}")
    print(f"Summary:")
    print(f"  Created: {created}")
    print(f"  Failed: {failed}")
    print(f"  Total: {len(pdf_staff)}")
    print(f"\n✓ Saved mapping to: {MAPPING_FILE}")

if __name__ == "__main__":
    main()
