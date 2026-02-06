# Historical Schedule Import - Using Database Migrations ✅

## Overview

This guide uses **Flyway database migrations** to import staff data - a much better approach than using the API!

## ✅ What's Already Done

1. **V3 Migration Created** - `backend/src/main/resources/db/migration/V3__Import_Historical_Staff.sql`
2. **Migration Executed** - 19 staff members imported into database
3. **Staff Mapping Generated** - `data/staff-mapping.json` automatically created with correct IDs
4. **Dry-Run Tested** - All 51 PDFs processed successfully with 0 failures

## 🎯 Current Status

**READY FOR PRODUCTION IMPORT!**

- ✅ 51 PDF schedules extracted and parsed
- ✅ 19 staff members in database (via Flyway migration)
- ✅ Staff mapping complete (all PDF names → database IDs)
- ✅ Backend running with migrations applied
- ✅ Dry-run test passed: 51 PDFs, 0 failures

## 🚀 Run the Import (Production)

### Option 1: Single Command
```bash
source venv/bin/activate && python3 scripts/import-schedules.py
```

### Option 2: Step-by-Step
```bash
# Activate Python environment
source venv/bin/activate

# Final dry-run check (optional)
python3 scripts/import-schedules.py --dry-run

# Run the actual import
python3 scripts/import-schedules.py

# Monitor progress
tail -f data/import-log.txt
```

## 📊 What Will Be Imported

- **Date Range**: August 18, 2025 → February 20, 2026
- **PDFs**: 51 weekly schedules
- **Staff**: 19 team members
- **Estimated Entries**: ~2,500+ schedule entries
- **Duration**: ~5-10 minutes (50ms rate limiting)

## 🔍 Verify Import

### Database Verification
```sql
-- Check date range
SELECT MIN(work_date) as start, MAX(work_date) as end FROM schedule_entries;
-- Expected: 2025-08-18 to 2026-02-20

-- Count by week
SELECT COUNT(DISTINCT weekly_schedule_id) as weeks FROM schedule_entries;
-- Expected: ~25 weeks

-- Count entries
SELECT COUNT(*) as total_entries FROM schedule_entries;
-- Expected: ~2,500+

-- Staff participation
SELECT s.full_name, COUNT(se.id) as entries
FROM staff s
LEFT JOIN schedule_entries se ON s.id = se.staff_id
GROUP BY s.id
ORDER BY entries DESC;
```

### Frontend Verification
1. Open http://localhost:4200
2. Login: `uwe` / `password123`
3. Navigate to **Schedule** tab
4. Browse weeks: Aug 2025 → Feb 2026
5. Verify entries match PDFs

## 📝 Migration Details

### V3 Migration Summary
- **File**: `V3__Import_Historical_Staff.sql`
- **Staff Imported**: 19
- **Groups**:
  - Französische Kindergruppe: 8 (3 core + 5 interns)
  - Minis: 4 (3 core + 1 trainee)
  - Portugiesische Kindergruppe: 7 (4 core + 3 interns)

### Staff IDs (Auto-Generated)
```
Omar Alaoui                   → ID 14
Isabel Sovic                  → ID 15
Prudence Noubissie Deutcheu   → ID 16
Bertheline Kock Nkoue...      → ID 17
Eunice Ellen Silva            → ID 18
Marion Dehnel                 → ID 19
Rick Otto                     → ID 20
Elisa de Sá Zua Caldeira      → ID 21
Camilla Mannshardt Oliveira   → ID 22
Alexandre Zua Caldeira        → ID 23
Letícia Viana                 → ID 24
Violetta Hristozova           → ID 25
Hendrixa Kogningbo            → ID 26
Fritz Sievers                 → ID 27
Yu Lou                        → ID 28
Karin Harboe                  → ID 29
João Pedro Amaral Ferreira    → ID 30
Iara Ferreira                 → ID 31
Camilla Weber                 → ID 32
```

## 🔧 Scripts Available

- `scripts/import-schedules.py` - Main import script
- `scripts/generate-staff-mapping.py` - Regenerate mapping if needed
- `scripts/extract-all-zips.sh` - Re-extract PDFs if needed
- `scripts/extract-staff-names.py` - Re-extract staff names

## ⚠️ Rollback (If Needed)

If you need to undo the import:

```sql
-- Delete schedule entries
DELETE FROM schedule_entries WHERE id > 0;

-- Delete weekly schedules
DELETE FROM weekly_schedules WHERE id > 0;

-- Verify cleanup
SELECT COUNT(*) FROM schedule_entries; -- Should be 0
SELECT COUNT(*) FROM weekly_schedules; -- Should be 0
```

To rollback staff migration:
```sql
-- Rollback to V2 (removes staff from migration)
DELETE FROM flyway_schema_history WHERE version = '3';
DELETE FROM staff WHERE created_by = 'migration';
```

Then restart backend to stay at V2.

## 📚 Additional Documentation

- **IMPORT_SCHEDULES_README.md** - Complete technical documentation
- **IMPLEMENTATION_STATUS.md** - Full implementation details
- **backend/src/main/resources/db/migration/V3__Import_Historical_Staff.sql** - Migration file

## ✅ Advantages of Migration Approach

1. **Version Controlled** - Migration is in Git
2. **Repeatable** - Can be run on any environment
3. **Atomic** - All staff created in one transaction
4. **No API Issues** - Bypasses circular reference problem
5. **Best Practice** - Proper database change management
6. **Easy Rollback** - Can rollback to V2 if needed

## 🎉 Ready to Go!

Everything is set up and tested. The staff migration has already run, the mapping is generated, and dry-run tests confirm everything works perfectly.

**Just run the import command and watch it populate 6 months of historical data!**

```bash
source venv/bin/activate
python3 scripts/import-schedules.py
```
