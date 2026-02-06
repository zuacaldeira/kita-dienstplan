# Quick Start: Import Historical Schedules

## Prerequisites

✅ Backend running on http://localhost:8080
✅ Python 3.x installed
✅ Virtual environment created with dependencies

## Quick Setup (First Time)

```bash
# 1. Install Python dependencies
cd /home/zuacaldeira/Development/praxis/kita-dienstplan-angular
python3 -m venv venv
source venv/bin/activate
pip install -r scripts/requirements.txt

# 2. Extract PDF files (already done)
cd scripts && ./extract-all-zips.sh
```

## Import Process

### Step 1: Create Staff in Database

Before importing, you need to create staff members. Edit the staff creation script or create manually via the frontend.

**Current staff to create** (from `data/staff-names-extracted.json`):
- Omar Alaoui (Erzieher fr.)
- Isabel Sovic (Erzieherin pt.)
- Prudence Noubissie Deutcheu (Erzieherin fr.)
- Bertheline Kock Nkoue Epse Tiwe (Erzieherin Mini)
- Eunice Ellen Silva (Erzieherin Mini)
- Rick Otto (Azubi)
- Marion Dehnel (Sozialassisstentin Mini)
- Elisa de Sá Zua Caldeira (Erzieherin pt.)
- Camilla Mannshardt Oliveira (Erzieherin pt.)
- Alexandre Zua Caldeira (Erzieherin pt.)
- And 12 more...

### Step 2: Update Staff Mapping

After creating staff, update `data/staff-mapping.json` with actual database IDs:

```json
{
  "omar_alaoui": 1,          // Replace with actual ID from database
  "isabel_sovic": 2,         // Replace with actual ID
  ...
}
```

### Step 3: Test Import (Dry Run)

```bash
source venv/bin/activate
python3 scripts/import-schedules.py --dry-run
```

Expected output:
```
✓ Loaded staff mapping for 22 staff members
Found 51 PDF files to process
✓ Parsing: PDienstplan 250825-250829r.pdf
  Week 35/2025: 2025-08-25 - 2025-08-29
  [DRY RUN] Would create weekly schedule...
  [DRY RUN] Would create entry: staff=10, day=1, date=2025-08-25...
...
PDFs processed: 51
PDFs failed: 0
```

### Step 4: Run Actual Import

Once dry-run looks good:

```bash
python3 scripts/import-schedules.py
```

Monitor progress:
```bash
# In another terminal
tail -f data/import-log.txt
```

### Step 5: Verify in Frontend

1. Open http://localhost:4200
2. Login as `uwe` / `password123`
3. Navigate to **Schedule** tab
4. Browse through weeks from August 2025 to February 2026
5. Verify staff schedules match PDFs

## Troubleshooting

### Backend Not Running
```bash
cd backend
mvn spring-boot:run
```

### Authentication Failed
Check credentials in scripts/import-schedules.py (default: uwe/password123)

### Staff Not Found Errors
Update `data/staff-mapping.json` with correct staff IDs from database

### Import Errors
Check `data/import-log.txt` for detailed error messages

## Current Status

- ✅ 47 ZIP files extracted
- ✅ 51 PDF schedules ready
- ✅ Staff names extracted (22 unique)
- ✅ Import script tested in dry-run mode
- ⏳ Waiting for staff creation in database
- ⏳ Ready for actual import

## Files Reference

- `data/extracted-schedules/` - 51 PDF schedules
- `data/staff-mapping.json` - Staff ID mapping (**EDIT THIS**)
- `data/staff-names-extracted.json` - List of all staff
- `scripts/import-schedules.py` - Main import script
- `data/import-log.txt` - Import log (created during import)

## Need Help?

See `IMPORT_SCHEDULES_README.md` for complete documentation.
