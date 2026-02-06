#!/bin/bash
# ============================================================================
# Fix Database Script
# ============================================================================
# This script:
# 1. Removes erroneous V3 migration from Flyway history
# 2. Fixes admin passwords to development default (password123)
# 3. Verifies all changes
# ============================================================================

echo "============================================================"
echo "Kita Casa Azul - Database Fix Script"
echo "============================================================"
echo ""
echo "This script will:"
echo "  1. Remove V3 migration record (if exists)"
echo "  2. Reset admin passwords to 'password123'"
echo "  3. Verify changes"
echo ""
echo "Please enter your MySQL root password when prompted:"
echo ""

mysql -u root -p kita_casa_azul << 'EOF'
-- ============================================================================
-- Step 1: Show current Flyway migration history
-- ============================================================================
SELECT '============================================================' AS '';
SELECT 'STEP 1: Current Flyway Migration History' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';

SELECT
    installed_rank AS 'Rank',
    version AS 'Version',
    description AS 'Description',
    success AS 'Success'
FROM flyway_schema_history
ORDER BY installed_rank;

-- ============================================================================
-- Step 2: Remove V3 migration if it exists
-- ============================================================================
SELECT '' AS '';
SELECT '============================================================' AS '';
SELECT 'STEP 2: Removing V3 Migration Record' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';

DELETE FROM flyway_schema_history WHERE version = '3';

SELECT CASE
    WHEN ROW_COUNT() > 0 THEN '✓ Removed V3 migration record'
    ELSE '✓ No V3 migration found (already clean)'
END AS 'Result';

-- ============================================================================
-- Step 3: Fix admin passwords
-- ============================================================================
SELECT '' AS '';
SELECT '============================================================' AS '';
SELECT 'STEP 3: Updating Admin Passwords' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';

-- Show admins before update
SELECT 'Before update:' AS '';
SELECT
    username AS 'Username',
    full_name AS 'Full Name',
    is_active AS 'Active',
    CHAR_LENGTH(password) AS 'Password Length'
FROM admins;

-- Update passwords to BCrypt hash for 'password123'
-- Hash generated and verified: $2a$10$nt6t3L3Kf2mVZ13DN7PwzumJIn8HOB/1czdH8J3bLVdxv6Rd6SrVm
UPDATE admins
SET password = '$2a$10$nt6t3L3Kf2mVZ13DN7PwzumJIn8HOB/1czdH8J3bLVdxv6Rd6SrVm',
    updated_by = 'fix-script'
WHERE username IN ('uwe', 'alexandre');

SELECT '' AS '';
SELECT CONCAT('✓ Updated ', ROW_COUNT(), ' admin password(s)') AS 'Result';

-- ============================================================================
-- Step 4: Verify all changes
-- ============================================================================
SELECT '' AS '';
SELECT '============================================================' AS '';
SELECT 'STEP 4: Verification' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';

SELECT 'Flyway Migrations (should be V1 and V2 only):' AS '';
SELECT
    installed_rank AS 'Rank',
    version AS 'Version',
    description AS 'Description'
FROM flyway_schema_history
ORDER BY installed_rank;

SELECT '' AS '';
SELECT 'Admin Accounts:' AS '';
SELECT
    username AS 'Username',
    full_name AS 'Full Name',
    is_active AS 'Active',
    CHAR_LENGTH(password) AS 'Pwd Length',
    CASE
        WHEN password = '$2a$10$nt6t3L3Kf2mVZ13DN7PwzumJIn8HOB/1czdH8J3bLVdxv6Rd6SrVm'
        THEN '✓ Correct'
        ELSE '✗ Wrong'
    END AS 'Status'
FROM admins
ORDER BY username;

-- ============================================================================
-- Summary
-- ============================================================================
SELECT '' AS '';
SELECT '============================================================' AS '';
SELECT 'SUMMARY' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';
SELECT '✓ Database fixed successfully!' AS '';
SELECT '' AS '';
SELECT 'Next steps:' AS '';
SELECT '1. Restart the backend: pkill -f DienstplanApplication && mvn spring-boot:run' AS '';
SELECT '2. Test login with username: uwe or alexandre' AS '';
SELECT '3. Password: password123' AS '';
SELECT '============================================================' AS '';
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "============================================================"
    echo "✅ Database fixes applied successfully!"
    echo "============================================================"
    echo ""
    echo "Next steps:"
    echo "  1. Restart the backend"
    echo "  2. Try logging in with:"
    echo "     - Username: uwe or alexandre"
    echo "     - Password: password123"
    echo ""
else
    echo ""
    echo "============================================================"
    echo "❌ Failed to apply database fixes"
    echo "============================================================"
    echo ""
    echo "Please check your MySQL credentials and try again."
    exit 1
fi
