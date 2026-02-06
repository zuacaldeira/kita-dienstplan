-- ============================================================================
-- Add Missing Columns for Flyway Migration
-- ============================================================================
-- This script adds the columns that are missing from the existing database
-- Run with: mysql -u root -p kita_casa_azul < add_missing_columns.sql
-- ============================================================================

USE kita_casa_azul;

-- Check and add last_login to admins table
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'kita_casa_azul'
AND TABLE_NAME = 'admins'
AND COLUMN_NAME = 'last_login';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE admins ADD COLUMN last_login TIMESTAMP NULL COMMENT "Last successful login timestamp" AFTER is_active;',
    'SELECT "Column last_login already exists in admins table" AS message;');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Check and add weekly_hours to staff table
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'kita_casa_azul'
AND TABLE_NAME = 'staff'
AND COLUMN_NAME = 'weekly_hours';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE staff ADD COLUMN weekly_hours DECIMAL(5,2) DEFAULT 40.00 COMMENT "Weekly working hours" AFTER employment_type;',
    'SELECT "Column weekly_hours already exists in staff table" AS message;');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verify the changes
SELECT '============================================================' AS '';
SELECT 'MIGRATION COMPLETED!' AS '';
SELECT '============================================================' AS '';

SELECT 'Checking admins table structure:' AS '';
SHOW COLUMNS FROM admins WHERE Field IN ('is_active', 'last_login', 'created_by');

SELECT 'Checking staff table structure:' AS '';
SHOW COLUMNS FROM staff WHERE Field IN ('employment_type', 'weekly_hours', 'email');

SELECT '============================================================' AS '';
SELECT 'You can now start the backend: mvn spring-boot:run' AS '';
SELECT '============================================================' AS '';
