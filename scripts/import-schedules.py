#!/usr/bin/env python3
"""
Import historical weekly schedules from PDF archives into the Kita Casa Azul system.

This script:
1. Parses PDF schedules extracted from ZIP archives
2. Maps staff names to database records
3. Creates weekly schedules and schedule entries via REST API
4. Logs progress and errors for verification
"""

import pdfplumber
import re
import sys
import json
import requests
from datetime import datetime, timedelta
from dateutil.parser import parse as parse_date
from pathlib import Path
import time

# Configuration
API_BASE_URL = "http://localhost:8080/api"
SCRIPT_DIR = Path(__file__).parent
PROJECT_DIR = SCRIPT_DIR.parent
PDF_DIR = PROJECT_DIR / "data" / "extracted-schedules"
STAFF_MAPPING_FILE = PROJECT_DIR / "data" / "staff-mapping.json"
IMPORT_LOG_FILE = PROJECT_DIR / "data" / "import-log.txt"

# German day names to day of week (1=Monday, 7=Sunday)
DAY_NAMES = {
    "Montag": 1,
    "Dienstag": 2,
    "Mittwoch": 3,
    "Donnerstag": 4,
    "Freitag": 5,
    "Samstag": 6,
    "Sonntag": 7
}

class ScheduleImporter:
    def __init__(self, username="uwe", password="password123", dry_run=False):
        self.username = username
        self.password = password
        self.dry_run = dry_run
        self.token = None
        self.staff_mapping = {}
        self.stats = {
            "pdfs_processed": 0,
            "pdfs_failed": 0,
            "weeks_created": 0,
            "entries_created": 0,
            "entries_failed": 0,
            "staff_not_found": set()
        }

    def log(self, message):
        """Log message to console and file"""
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        log_msg = f"[{timestamp}] {message}"
        print(log_msg)
        with open(IMPORT_LOG_FILE, "a") as f:
            f.write(log_msg + "\n")

    def authenticate(self):
        """Authenticate with backend and get JWT token"""
        self.log("Authenticating with backend...")
        try:
            response = requests.post(
                f"{API_BASE_URL}/auth/login",
                json={"username": self.username, "password": self.password},
                headers={"Content-Type": "application/json"}
            )
            response.raise_for_status()
            data = response.json()
            self.token = data.get("token")
            self.log(f"✓ Authentication successful (token length: {len(self.token)})")
            return True
        except Exception as e:
            self.log(f"✗ Authentication failed: {e}")
            return False

    def get_headers(self):
        """Get HTTP headers with JWT token"""
        return {
            "Authorization": f"Bearer {self.token}",
            "Content-Type": "application/json"
        }

    def load_staff_mapping(self):
        """Load staff name to ID mapping"""
        if not STAFF_MAPPING_FILE.exists():
            self.log(f"Staff mapping file not found: {STAFF_MAPPING_FILE}")
            self.log("Please create staff mapping file first")
            return False

        with open(STAFF_MAPPING_FILE) as f:
            self.staff_mapping = json.load(f)

        self.log(f"✓ Loaded staff mapping for {len(self.staff_mapping)} staff members")
        return True

    def parse_filename(self, filename):
        """Extract date range from filename like 'PDienstplan 250825-250829p.pdf'"""
        # Match pattern: YYMMDD-YYMMDD
        match = re.search(r'(\d{6})-(\d{6})', filename)
        if not match:
            return None, None

        start_str = match.group(1)
        end_str = match.group(2)

        # Parse dates (assuming 20XX for years 00-50, 19XX for 51-99)
        year_start = int(start_str[0:2])
        year_start = 2000 + year_start if year_start < 50 else 1900 + year_start

        year_end = int(end_str[0:2])
        year_end = 2000 + year_end if year_end < 50 else 1900 + year_end

        start_date = datetime(
            year_start,
            int(start_str[2:4]),  # month
            int(start_str[4:6])   # day
        )

        end_date = datetime(
            year_end,
            int(end_str[2:4]),
            int(end_str[4:6])
        )

        return start_date, end_date

    def get_iso_week(self, date):
        """Get ISO week number and year"""
        iso_cal = date.isocalendar()
        return iso_cal[0], iso_cal[1]  # year, week

    def parse_time(self, time_str):
        """Parse time string like '9:15' or '09:15' to HH:MM format"""
        if not time_str or time_str.strip() == "":
            return None

        time_str = time_str.strip()
        match = re.match(r'(\d{1,2}):(\d{2})', time_str)
        if match:
            hour = int(match.group(1))
            minute = int(match.group(2))
            return f"{hour:02d}:{minute:02d}"
        return None

    def parse_day_cell(self, cell_text):
        """Parse a day cell containing times and status

        Returns: (start_time, end_time, status, notes)
        """
        if not cell_text or cell_text.strip() == "":
            return None, None, "NORMAL", None

        cell_text = cell_text.strip().lower()

        # Check for status keywords
        if "frei" in cell_text:
            return None, None, "FREI", "frei"
        if "krank" in cell_text:
            return None, None, "KRANK", "krank"
        if "schule" in cell_text or "fachschule" in cell_text:
            return None, None, "FORTBILDUNG", "Schule"
        if "urlaub" in cell_text:
            return None, None, "URLAUB", "Urlaub"

        # Parse times: look for pattern like "9:15 17:00" or "9:15 17:00\n..."
        # Extract first line which should contain start and end times
        lines = cell_text.split('\n')
        if not lines:
            return None, None, "NORMAL", None

        first_line = lines[0].strip()

        # Try to extract two times from first line
        times = re.findall(r'(\d{1,2}:\d{2})', first_line)
        if len(times) >= 2:
            start_time = self.parse_time(times[0])
            end_time = self.parse_time(times[1])
            return start_time, end_time, "NORMAL", None
        elif len(times) == 1:
            # Only one time found, might be partial entry
            # Check if it's the same time repeated (like "8:30 8:30")
            if first_line.count(times[0]) >= 2:
                return None, None, "FREI", "frei"

        return None, None, "NORMAL", None

    def parse_staff_row(self, row):
        """Parse a staff row from the table

        Returns: {
            'last_name': str,
            'first_name': str,
            'role': str,
            'monday': (start, end, status, notes),
            'tuesday': ...,
            ...
        }
        """
        if not row or len(row) < 7:
            return None

        # Column 0: LastName\nRole
        col0 = row[0]
        if not col0 or col0.strip() == "":
            return None

        # Column 1: FirstName\nArbeitszeit/Pause
        col1 = row[1]
        if not col1 or "Arbeitszeit" not in col1:
            return None

        # Parse name and role
        parts0 = col0.split('\n')
        if len(parts0) < 2:
            return None

        last_name = parts0[0].strip()
        role = parts0[1].strip()

        # Parse first name
        parts1 = col1.split('\n')
        first_name = parts1[0].strip()

        # Parse weekdays (columns 2-6)
        days_data = {}
        for i, day_name in enumerate(['monday', 'tuesday', 'wednesday', 'thursday', 'friday']):
            col_idx = i + 2
            if col_idx < len(row):
                cell_text = row[col_idx]
                days_data[day_name] = self.parse_day_cell(cell_text)

        return {
            'last_name': last_name,
            'first_name': first_name,
            'role': role,
            **days_data
        }

    def find_staff_id(self, first_name, last_name):
        """Find staff ID from mapping by name"""
        # Try exact match first
        key = f"{first_name}_{last_name}".lower()
        if key in self.staff_mapping:
            return self.staff_mapping[key]

        # Try just first name
        for k, v in self.staff_mapping.items():
            if k.lower().startswith(first_name.lower()):
                return v

        return None

    def parse_pdf(self, pdf_path):
        """Parse a single PDF schedule

        Returns: {
            'filename': str,
            'start_date': datetime,
            'end_date': datetime,
            'year': int,
            'week_number': int,
            'staff_entries': [...]
        }
        """
        filename = pdf_path.name
        self.log(f"\nParsing: {filename}")

        # Parse dates from filename
        start_date, end_date = self.parse_filename(filename)
        if not start_date:
            self.log(f"  ✗ Could not parse dates from filename")
            return None

        year, week_number = self.get_iso_week(start_date)
        self.log(f"  Week {week_number}/{year}: {start_date.date()} - {end_date.date()}")

        # Parse PDF
        staff_entries = []
        try:
            with pdfplumber.open(pdf_path) as pdf:
                page = pdf.pages[0]
                tables = page.extract_tables()

                if not tables:
                    self.log(f"  ✗ No tables found in PDF")
                    return None

                table = tables[0]
                self.log(f"  Found table with {len(table)} rows")

                # Parse each row
                for row_num, row in enumerate(table):
                    # Skip section headers and empty rows
                    if not row or len(row) < 7:
                        continue

                    # Check if this is a staff row
                    staff_data = self.parse_staff_row(row)
                    if staff_data:
                        staff_entries.append(staff_data)

                self.log(f"  ✓ Parsed {len(staff_entries)} staff entries")

        except Exception as e:
            self.log(f"  ✗ Error parsing PDF: {e}")
            return None

        return {
            'filename': filename,
            'start_date': start_date,
            'end_date': end_date,
            'year': year,
            'week_number': week_number,
            'staff_entries': staff_entries
        }

    def create_weekly_schedule(self, year, week_number):
        """Create or get weekly schedule record"""
        if self.dry_run:
            self.log(f"  [DRY RUN] Would create weekly schedule for {year}/W{week_number}")
            return {"id": f"dry-run-{year}-{week_number}"}

        try:
            # Check if already exists
            response = requests.get(
                f"{API_BASE_URL}/weekly-schedules/{year}/{week_number}",
                headers=self.get_headers()
            )

            if response.status_code == 200:
                data = response.json()
                self.log(f"  ✓ Weekly schedule already exists: ID {data['id']}")
                return data

            # Create new weekly schedule
            response = requests.post(
                f"{API_BASE_URL}/weekly-schedules",
                json={
                    "year": year,
                    "weekNumber": week_number,
                    "notes": "Imported from PDF archive"
                },
                headers=self.get_headers()
            )
            response.raise_for_status()
            data = response.json()
            self.log(f"  ✓ Created weekly schedule: ID {data['id']}")
            self.stats["weeks_created"] += 1
            return data

        except Exception as e:
            self.log(f"  ✗ Error creating weekly schedule: {e}")
            return None

    def create_schedule_entry(self, weekly_schedule_id, staff_id, day_of_week, work_date,
                             start_time, end_time, status, notes):
        """Create a single schedule entry"""
        if self.dry_run:
            self.log(f"    [DRY RUN] Would create entry: staff={staff_id}, day={day_of_week}, "
                    f"date={work_date}, {start_time}-{end_time}, status={status}")
            return True

        try:
            response = requests.post(
                f"{API_BASE_URL}/schedules/entries",
                json={
                    "weeklyScheduleId": weekly_schedule_id,
                    "staffId": staff_id,
                    "dayOfWeek": day_of_week,
                    "workDate": work_date.strftime("%Y-%m-%d"),
                    "startTime": start_time,
                    "endTime": end_time,
                    "status": status,
                    "notes": notes
                },
                headers=self.get_headers()
            )

            if response.status_code == 201:
                self.stats["entries_created"] += 1
                return True
            else:
                self.log(f"    ✗ Failed to create entry: HTTP {response.status_code}")
                self.stats["entries_failed"] += 1
                return False

        except Exception as e:
            self.log(f"    ✗ Error creating entry: {e}")
            self.stats["entries_failed"] += 1
            return False

    def import_schedule(self, schedule_data):
        """Import a parsed schedule into the database"""
        filename = schedule_data['filename']
        year = schedule_data['year']
        week_number = schedule_data['week_number']
        start_date = schedule_data['start_date']

        self.log(f"\nImporting {filename}")

        # Create weekly schedule
        weekly_schedule = self.create_weekly_schedule(year, week_number)
        if not weekly_schedule:
            self.log(f"  ✗ Failed to create weekly schedule, skipping")
            self.stats["pdfs_failed"] += 1
            return False

        weekly_schedule_id = weekly_schedule['id']

        # Import each staff member's entries
        entries_created = 0
        for staff_data in schedule_data['staff_entries']:
            first_name = staff_data['first_name']
            last_name = staff_data['last_name']

            # Find staff ID
            staff_id = self.find_staff_id(first_name, last_name)
            if not staff_id:
                self.log(f"  ⚠ Staff not found in mapping: {first_name} {last_name}")
                self.stats["staff_not_found"].add(f"{first_name} {last_name}")
                continue

            # Create entries for each working day
            for day_idx, day_name in enumerate(['monday', 'tuesday', 'wednesday', 'thursday', 'friday']):
                day_of_week = day_idx + 1
                work_date = start_date + timedelta(days=day_idx)

                day_data = staff_data.get(day_name)
                if not day_data:
                    continue

                start_time, end_time, status, notes = day_data

                # Skip if no valid times and status is NORMAL (empty entry)
                if not start_time and not end_time and status == "NORMAL":
                    continue

                # Create entry
                success = self.create_schedule_entry(
                    weekly_schedule_id, staff_id, day_of_week, work_date,
                    start_time, end_time, status, notes
                )

                if success:
                    entries_created += 1

                # Rate limiting
                if not self.dry_run:
                    time.sleep(0.05)  # 50ms between entries

        self.log(f"  ✓ Created {entries_created} schedule entries")
        self.stats["pdfs_processed"] += 1
        return True

    def run(self):
        """Main import process"""
        self.log("="*60)
        self.log("Starting Schedule Import")
        self.log("="*60)
        self.log(f"PDF Directory: {PDF_DIR.resolve()}")
        self.log(f"Dry Run: {self.dry_run}")
        self.log("")

        # Authenticate
        if not self.dry_run:
            if not self.authenticate():
                return False

        # Load staff mapping
        if not self.load_staff_mapping():
            return False

        # Find all PDF files
        pdf_files = sorted(PDF_DIR.glob("PDienstplan*.pdf"))
        self.log(f"Found {len(pdf_files)} PDF files to process\n")

        # Process each PDF
        for pdf_path in pdf_files:
            schedule_data = self.parse_pdf(pdf_path)
            if schedule_data:
                self.import_schedule(schedule_data)

        # Print summary
        self.log("\n" + "="*60)
        self.log("Import Complete - Summary")
        self.log("="*60)
        self.log(f"PDFs processed: {self.stats['pdfs_processed']}")
        self.log(f"PDFs failed: {self.stats['pdfs_failed']}")
        self.log(f"Weekly schedules created: {self.stats['weeks_created']}")
        self.log(f"Schedule entries created: {self.stats['entries_created']}")
        self.log(f"Schedule entries failed: {self.stats['entries_failed']}")

        if self.stats["staff_not_found"]:
            self.log(f"\nStaff not found in mapping ({len(self.stats['staff_not_found'])}):")
            for name in sorted(self.stats["staff_not_found"]):
                self.log(f"  - {name}")

        return True


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="Import schedules from PDF archives")
    parser.add_argument("--dry-run", action="store_true", help="Test mode without API calls")
    parser.add_argument("--username", default="uwe", help="Admin username (default: uwe)")
    parser.add_argument("--password", default="password123", help="Admin password")

    args = parser.parse_args()

    importer = ScheduleImporter(
        username=args.username,
        password=args.password,
        dry_run=args.dry_run
    )

    success = importer.run()
    sys.exit(0 if success else 1)
