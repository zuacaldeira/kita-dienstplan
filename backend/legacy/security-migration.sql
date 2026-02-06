-- ============================================================================
-- KITA CASA AZUL - SECURITY AND AUDITING MIGRATION (MySQL)
-- ============================================================================
-- This script adds:
-- 1. Admins table for authentication
-- 2. Audit columns (created_by, updated_by) to existing tables
-- 3. Initial admin users (Uwe and Alexandre)
-- ============================================================================

-- ============================================================================
-- 1. CREATE ADMINS TABLE
-- ============================================================================

CREATE TABLE IF NOT EXISTS admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed password',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_admins_username (username),
    INDEX idx_admins_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Admin users for system authentication';

-- ============================================================================
-- 2. ADD AUDIT COLUMNS TO EXISTING TABLES
-- ============================================================================

-- Add audit columns to weekly_schedules
ALTER TABLE weekly_schedules
ADD COLUMN IF NOT EXISTS created_by VARCHAR(50) COMMENT 'Admin who created this record',
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50) COMMENT 'Admin who last updated this record';

-- Add audit columns to age_groups
ALTER TABLE `age_groups`
ADD COLUMN IF NOT EXISTS created_by VARCHAR(50) COMMENT 'Admin who created this record',
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50) COMMENT 'Admin who last updated this record';

-- Add audit columns to staff
ALTER TABLE staff
ADD COLUMN IF NOT EXISTS created_by VARCHAR(50) COMMENT 'Admin who created this record',
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50) COMMENT 'Admin who last updated this record';

-- Add audit columns to schedule_entries
ALTER TABLE schedule_entries
ADD COLUMN IF NOT EXISTS created_by VARCHAR(50) COMMENT 'Admin who created this record',
ADD COLUMN IF NOT EXISTS updated_by VARCHAR(50) COMMENT 'Admin who last updated this record';

-- ============================================================================
-- 3. INSERT INITIAL ADMIN USERS
-- ============================================================================

-- Password for both admins: "password123" (CHANGE THIS IN PRODUCTION!)
-- BCrypt hash for "password123"
SET @default_password = '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG';

-- Insert Uwe
INSERT INTO admins (username, password, full_name, email, is_active)
VALUES ('uwe', @default_password, 'Otto Uwe', 'uwe@kitacasaazul.de', TRUE)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email);

-- Insert Alexandre
INSERT INTO admins (username, password, full_name, email, is_active)
VALUES ('alexandre', @default_password, 'Zua Caldeira Alexandre', 'alexandre@kitacasaazul.de', TRUE)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    email = VALUES(email);

-- ============================================================================
-- 4. VERIFY INSTALLATION
-- ============================================================================

-- Check admins table
SELECT id, username, full_name, email, is_active, created_at
FROM admins;

-- Check audit columns
DESCRIBE weekly_schedules;
DESCRIBE `age_groups`;
DESCRIBE staff;
DESCRIBE schedule_entries;

-- ============================================================================
-- IMPORTANT NOTES
-- ============================================================================

/*
DEFAULT PASSWORDS:
- Username: uwe        Password: password123
- Username: alexandre  Password: password123

⚠️  SECURITY WARNING ⚠️
The default password is "password123" for both admins.

IMPORTANT: Change these passwords immediately after first login!

To change a password, use the BCrypt generator:
https://bcrypt-generator.com/

Then update:
UPDATE admins SET password = 'YOUR_BCRYPT_HASH' WHERE username = 'uwe';
UPDATE admins SET password = 'YOUR_BCRYPT_HASH' WHERE username = 'alexandre';

AUDIT TRACKING:
All database modifications will now automatically record:
- created_by: Username of admin who created the record
- updated_by: Username of admin who last modified the record

This happens automatically via Spring Data JPA @CreatedBy and @LastModifiedBy annotations.
*/
