#!/usr/bin/env python3
"""
Generate SQL INSERT statements for schedule import
Bypasses API issues by generating direct SQL
"""

import pdfplumber
import re
import json
from datetime import datetime, timedelta
from pathlib import Path

# Configuration
SCRIPT_DIR = Path(__file__).parent
PROJECT_DIR = SCRIPT_DIR.parent
PDF_DIR = PROJECT_DIR / "data" / "extracted-schedules"
STAFF_MAPPING_FILE = PROJECT_DIR / "data" / "staff-mapping.json"
OUTPUT_SQL_FILE = PROJECT_DIR / "backend" / "src" / "main" / "resources" / "db" / "migration" / "V4__Import_Historical_Schedules.sql"

# Load staff mapping
with open(STAFF_MAPPING_FILE) as f:
    STAFF_MAPPING = json.load(f)

def parse_filename(filename):
    """Extract date range from filename"""
    match = re.search(r'(\d{6})-(\d{6})', filename)
    if not match:
        return None, None

    start_str = match.group(1)
    end_str = match.group(2)

    year_start = int(start_str[0:2])
    year_start = 2000 + year_start if year_start < 50 else 1900 + year_start

    year_end = int(end_str[0:2])
    year_end = 2000 + year_end if year_end < 50 else 1900 + year_end

    start_date = datetime(year_start, int(start_str[2:4]), int(start_str[4:6]))
    end_date = datetime(year_end, int(end_str[2:4]), int(end_str[4:6]))

    return start_date, end_date

def get_iso_week(date):
    """Get ISO week number and year"""
    iso_cal = date.isocalendar()
    return iso_cal[0], iso_cal[1]

def parse_time(time_str):
    """Parse time string to HH:MM format"""
    if not time_str or time_str.strip() == "":
        return None
    time_str = time_str.strip()
    match = re.match(r'(\d{1,2}):(\d{2})', time_str)
    if match:
        return f"{int(match.group(1)):02d}:{int(match.group(2)):02d}:00"
    return None

def parse_day_cell(cell_text):
    """Parse day cell data"""
    if not cell_text or cell_text.strip() == "":
        return None, None, "NORMAL", None

    cell_text = cell_text.strip().lower()

    if "frei" in cell_text:
        return None, None, "FREI", "frei"
    if "krank" in cell_text:
        return None, None, "KRANK", "krank"
    if "schule" in cell_text or "fachschule" in cell_text:
        return None, None, "FORTBILDUNG", "Schule"
    if "urlaub" in cell_text:
        return None, None, "URLAUB", "Urlaub"

    lines = cell_text.split('\n')
    if not lines:
        return None, None, "NORMAL", None

    first_line = lines[0].strip()
    times = re.findall(r'(\d{1,2}:\d{2})', first_line)

    if len(times) >= 2:
        start_time = parse_time(times[0])
        end_time = parse_time(times[1])
        return start_time, end_time, "NORMAL", None
    elif len(times) == 1 and first_line.count(times[0]) >= 2:
        return None, None, "FREI", "frei"

    return None, None, "NORMAL", None

def parse_staff_row(row):
    """Parse staff row from table"""
    if not row or len(row) < 7:
        return None

    col0 = row[0]
    col1 = row[1]

    if not col0 or not col1 or "Arbeitszeit" not in str(col1):
        return None

    parts0 = str(col0).split('\n')
    if len(parts0) < 2:
        return None

    last_name = parts0[0].strip()
    parts1 = str(col1).split('\n')
    first_name = parts1[0].strip()

    days_data = {}
    for i, day_name in enumerate(['monday', 'tuesday', 'wednesday', 'thursday', 'friday']):
        col_idx = i + 2
        if col_idx < len(row):
            days_data[day_name] = parse_day_cell(row[col_idx])

    return {'first_name': first_name, 'last_name': last_name, **days_data}

def find_staff_id(first_name, last_name):
    """Find staff ID from mapping"""
    key = f"{first_name}_{last_name}".lower()
    if key in STAFF_MAPPING:
        return STAFF_MAPPING[key]
    for k, v in STAFF_MAPPING.items():
        if k.lower().startswith(first_name.lower()):
            return v
    return None

def escape_sql(text):
    """Escape text for SQL"""
    if text is None:
        return "NULL"
    return "'" + str(text).replace("'", "''") + "'"

def main():
    print("Generating SQL for schedule import...")
    print(f"PDF Directory: {PDF_DIR}")
    print(f"Output: {OUTPUT_SQL_FILE}\n")

    pdf_files = sorted(PDF_DIR.glob("PDienstplan*.pdf"))
    print(f"Found {len(pdf_files)} PDF files\n")

    # Track processed weeks to avoid duplicates
    processed_weeks = set()
    weekly_schedules = []
    schedule_entries = []

    for pdf_path in pdf_files:
        start_date, end_date = parse_filename(pdf_path.name)
        if not start_date:
            continue

        year, week_number = get_iso_week(start_date)
        week_key = (year, week_number)

        # Skip if we already processed this week (prefer 'r' over 'p')
        if week_key in processed_weeks and 'p' in pdf_path.name:
            print(f"  Skipping {pdf_path.name} (duplicate, prefer 'r' version)")
            continue

        print(f"Processing: {pdf_path.name} (Week {week_number}/{year})")

        try:
            with pdfplumber.open(pdf_path) as pdf:
                page = pdf.pages[0]
                tables = page.extract_tables()

                if not tables:
                    continue

                table = tables[0]

                # Add weekly schedule if not already added
                if week_key not in processed_weeks:
                    weekly_schedules.append({
                        'year': year,
                        'week_number': week_number,
                        'start_date': start_date.strftime('%Y-%m-%d'),
                        'end_date': end_date.strftime('%Y-%m-%d'),
                        'notes': f'Imported from {pdf_path.name}'
                    })
                    processed_weeks.add(week_key)

                # Parse staff entries
                for row in table:
                    staff_data = parse_staff_row(row)
                    if not staff_data:
                        continue

                    staff_id = find_staff_id(staff_data['first_name'], staff_data['last_name'])
                    if not staff_id:
                        print(f"    ⚠ Staff not found: {staff_data['first_name']} {staff_data['last_name']}")
                        continue

                    # Create entries for each day
                    for day_idx, day_name in enumerate(['monday', 'tuesday', 'wednesday', 'thursday', 'friday']):
                        day_of_week = day_idx + 1
                        work_date = start_date + timedelta(days=day_idx)
                        day_data = staff_data.get(day_name)

                        if not day_data:
                            continue

                        start_time, end_time, status, notes = day_data

                        # Skip empty NORMAL entries
                        if not start_time and not end_time and status == "NORMAL":
                            continue

                        schedule_entries.append({
                            'year': year,
                            'week_number': week_number,
                            'staff_id': staff_id,
                            'day_of_week': day_of_week,
                            'work_date': work_date.strftime('%Y-%m-%d'),
                            'start_time': start_time,
                            'end_time': end_time,
                            'status': status,
                            'notes': notes
                        })

        except Exception as e:
            print(f"  ✗ Error: {e}")

    print(f"\n✓ Processed {len(processed_weeks)} unique weeks")
    print(f"✓ Generated {len(schedule_entries)} schedule entries\n")

    # Generate SQL
    print(f"Writing SQL to {OUTPUT_SQL_FILE}...")

    with open(OUTPUT_SQL_FILE, 'w') as f:
        f.write("-- " + "="*76 + "\n")
        f.write("-- KITA CASA AZUL - Import Historical Schedules\n")
        f.write("-- " + "="*76 + "\n")
        f.write("-- Flyway Migration V4\n")
        f.write("-- Description: Imports historical weekly schedules from PDF archives\n")
        f.write("-- Date Range: August 2025 - February 2026\n")
        f.write(f"-- Total Weeks: {len(weekly_schedules)}\n")
        f.write(f"-- Total Entries: {len(schedule_entries)}\n")
        f.write("-- Source: PDF schedules from data/weekly-schedules\n")
        f.write("-- Generated by: scripts/generate-schedule-sql.py\n")
        f.write("-- " + "="*76 + "\n\n")

        # Weekly schedules
        f.write("-- " + "="*76 + "\n")
        f.write("-- WEEKLY SCHEDULES\n")
        f.write("-- " + "="*76 + "\n\n")

        for ws in sorted(weekly_schedules, key=lambda x: (x['year'], x['week_number'])):
            f.write(f"-- Week {ws['week_number']}/{ws['year']}: {ws['start_date']} - {ws['end_date']}\n")
            f.write("INSERT INTO weekly_schedules (year, week_number, start_date, end_date, notes, created_by)\n")
            f.write("SELECT * FROM (\n")
            f.write(f"    SELECT {ws['year']} as year,\n")
            f.write(f"           {ws['week_number']} as week_number,\n")
            f.write(f"           '{ws['start_date']}' as start_date,\n")
            f.write(f"           '{ws['end_date']}' as end_date,\n")
            f.write(f"           {escape_sql(ws['notes'])} as notes,\n")
            f.write(f"           'migration' as created_by\n")
            f.write(") AS tmp\n")
            f.write(f"WHERE NOT EXISTS (SELECT 1 FROM weekly_schedules WHERE year = {ws['year']} AND week_number = {ws['week_number']});\n\n")

        # Schedule entries
        f.write("\n-- " + "="*76 + "\n")
        f.write("-- SCHEDULE ENTRIES\n")
        f.write("-- " + "="*76 + "\n\n")

        entries_by_week = {}
        for entry in schedule_entries:
            key = (entry['year'], entry['week_number'])
            if key not in entries_by_week:
                entries_by_week[key] = []
            entries_by_week[key].append(entry)

        for (year, week_number), entries in sorted(entries_by_week.items()):
            f.write(f"-- Week {week_number}/{year}: {len(entries)} entries\n")

            for entry in entries:
                start_time_val = f"'{entry['start_time']}'" if entry['start_time'] else "NULL"
                end_time_val = f"'{entry['end_time']}'" if entry['end_time'] else "NULL"
                notes_val = escape_sql(entry['notes'])

                f.write(f"INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, notes, created_by)\n")
                f.write(f"SELECT ws.id, {entry['staff_id']}, {entry['day_of_week']}, '{entry['work_date']}', {start_time_val}, {end_time_val}, '{entry['status']}', {notes_val}, 'migration'\n")
                f.write(f"FROM weekly_schedules ws\n")
                f.write(f"WHERE ws.year = {year} AND ws.week_number = {week_number}\n")
                f.write(f"AND NOT EXISTS (SELECT 1 FROM schedule_entries WHERE weekly_schedule_id = ws.id AND staff_id = {entry['staff_id']} AND day_of_week = {entry['day_of_week']});\n\n")

        f.write("-- " + "="*76 + "\n")
        f.write("-- MIGRATION SUMMARY\n")
        f.write("-- " + "="*76 + "\n")
        f.write(f"-- Total weekly schedules: {len(weekly_schedules)}\n")
        f.write(f"-- Total schedule entries: {len(schedule_entries)}\n")
        f.write("-- Date range: August 2025 - February 2026\n")
        f.write("-- " + "="*76 + "\n")

    print(f"✓ SQL migration created: {OUTPUT_SQL_FILE}")
    print(f"✓ {len(weekly_schedules)} weekly schedules")
    print(f"✓ {len(schedule_entries)} schedule entries")
    print("\nTo apply the migration:")
    print("  1. Restart the backend (mvn spring-boot:run)")
    print("  2. Flyway will automatically apply V4 migration")
    print("  3. Verify in dashboard at http://localhost:4200")

if __name__ == "__main__":
    main()
