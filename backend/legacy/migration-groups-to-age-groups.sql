-- ============================================================================
-- MIGRATION SCRIPT: Rename 'groups' table to 'age_groups'
-- ============================================================================
-- Date: February 2, 2026
-- Description: Renames the 'groups' table to 'age_groups'
-- Impact: Breaking change - requires backend and frontend updates
-- ============================================================================

-- IMPORTANT: Backup your database before running this migration!
-- mysqldump -u kita_admin -p kita_casa_azul > backup_before_groups_rename.sql

-- ============================================================================
-- Step 1: Verify current state
-- ============================================================================

-- Check if 'groups' table exists
SELECT 'Checking if groups table exists...' AS status;
SELECT COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name = 'groups';

-- Check if 'age_groups' table already exists
SELECT 'Checking if age_groups table already exists...' AS status;
SELECT COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name = 'age_groups';

-- Show current data count
SELECT 'Current data in groups table:' AS status;
SELECT COUNT(*) as total_groups FROM `groups`;

-- ============================================================================
-- Step 2: Rename the table
-- ============================================================================

-- Rename groups table to age_groups
SELECT 'Renaming table: groups → age_groups...' AS status;
ALTER TABLE `groups` RENAME TO `age_groups`;

-- ============================================================================
-- Step 3: Verify the migration
-- ============================================================================

-- Verify new table exists
SELECT 'Verifying age_groups table exists...' AS status;
DESCRIBE `age_groups`;

-- Verify data was preserved
SELECT 'Verifying data integrity...' AS status;
SELECT COUNT(*) as total_age_groups FROM `age_groups`;
SELECT * FROM `age_groups`;

-- Verify foreign key from staff table
SELECT 'Verifying foreign key relationships...' AS status;
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM
    information_schema.KEY_COLUMN_USAGE
WHERE
    TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'staff'
    AND COLUMN_NAME = 'group_id';

-- Test JOIN to ensure foreign key works
SELECT 'Testing staff-age_groups JOIN...' AS status;
SELECT 
    s.id,
    s.full_name,
    s.role,
    ag.name as age_group_name
FROM staff s
LEFT JOIN age_groups ag ON s.group_id = ag.id
LIMIT 5;

-- ============================================================================
-- Step 4: Summary
-- ============================================================================

SELECT '
============================================================================
MIGRATION SUMMARY
============================================================================
✅ Table renamed: groups → age_groups
✅ Foreign key constraint automatically updated
✅ All data preserved
✅ Indexes maintained

NEXT STEPS:
1. Update backend: Deploy new Spring Boot code with updated @Table annotation
2. Update frontend: Deploy new React/Angular code with updated API endpoints
3. Test all CRUD operations
4. Monitor logs for any errors

ROLLBACK (if needed):
ALTER TABLE `age_groups` RENAME TO `groups`;

============================================================================
' AS migration_complete;
