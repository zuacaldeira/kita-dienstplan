#!/usr/bin/env python3
"""
Extract unique staff names from all PDFs to create mapping file
"""

import pdfplumber
import json
from pathlib import Path
from collections import defaultdict

SCRIPT_DIR = Path(__file__).parent
PROJECT_DIR = SCRIPT_DIR.parent
PDF_DIR = PROJECT_DIR / "data" / "extracted-schedules"
OUTPUT_FILE = PROJECT_DIR / "data" / "staff-names-extracted.json"

def parse_staff_row(row):
    """Parse a staff row to extract name and role"""
    if not row or len(row) < 2:
        return None

    col0 = row[0]
    col1 = row[1]

    if not col0 or not col1:
        return None

    # Check if this is a staff row (has "Arbeitszeit" in second column)
    if "Arbeitszeit" not in str(col1):
        return None

    # Parse name and role from first column
    parts0 = str(col0).split('\n')
    if len(parts0) < 2:
        return None

    last_name = parts0[0].strip()
    role = parts0[1].strip()

    # Parse first name from second column
    parts1 = str(col1).split('\n')
    first_name = parts1[0].strip()

    return {
        'first_name': first_name,
        'last_name': last_name,
        'role': role
    }

def extract_staff_from_pdf(pdf_path):
    """Extract all staff members from a PDF"""
    staff = []

    try:
        with pdfplumber.open(pdf_path) as pdf:
            page = pdf.pages[0]
            tables = page.extract_tables()

            if not tables:
                return []

            table = tables[0]

            for row in table:
                staff_data = parse_staff_row(row)
                if staff_data:
                    staff.append(staff_data)

    except Exception as e:
        print(f"Error parsing {pdf_path.name}: {e}")

    return staff

def main():
    print(f"Extracting staff names from PDFs in: {PDF_DIR.resolve()}")

    # Find all PDFs
    pdf_files = sorted(PDF_DIR.glob("PDienstplan*.pdf"))
    print(f"Found {len(pdf_files)} PDF files\n")

    # Extract staff from all PDFs
    all_staff = defaultdict(lambda: {'count': 0, 'roles': set()})

    for pdf_path in pdf_files:
        staff_list = extract_staff_from_pdf(pdf_path)
        for staff in staff_list:
            key = f"{staff['first_name']}_{staff['last_name']}"
            all_staff[key]['first_name'] = staff['first_name']
            all_staff[key]['last_name'] = staff['last_name']
            all_staff[key]['roles'].add(staff['role'])
            all_staff[key]['count'] += 1

    # Convert to serializable format
    output = {}
    for key, data in sorted(all_staff.items()):
        output[key] = {
            'first_name': data['first_name'],
            'last_name': data['last_name'],
            'roles': list(data['roles']),
            'appearances': data['count']
        }

    # Save to file
    with open(OUTPUT_FILE, 'w') as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"\n✓ Extracted {len(output)} unique staff members")
    print(f"✓ Saved to: {OUTPUT_FILE.resolve()}")

    # Print summary
    print("\nStaff members found:")
    print("-" * 60)
    for key, data in sorted(output.items(), key=lambda x: x[1]['last_name']):
        print(f"{data['first_name']:15} {data['last_name']:20} {', '.join(data['roles'])}")

if __name__ == "__main__":
    main()
