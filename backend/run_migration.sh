#!/bin/bash
# ============================================================================
# Run Database Migration
# ============================================================================
# This script adds missing columns and starts the backend with Flyway
# ============================================================================

echo "============================================================"
echo "Kita Casa Azul - Database Migration"
echo "============================================================"
echo ""

# Add missing columns
echo "Step 1: Adding missing database columns..."
echo "Please enter your MySQL root password when prompted:"
echo ""

mysql -u root -p kita_casa_azul << 'EOF'
-- Add last_login to admins if it doesn't exist
ALTER TABLE admins ADD COLUMN IF NOT EXISTS last_login TIMESTAMP NULL COMMENT 'Last successful login timestamp' AFTER is_active;

-- Add weekly_hours to staff if it doesn't exist
ALTER TABLE staff ADD COLUMN IF NOT EXISTS weekly_hours DECIMAL(5,2) DEFAULT 40.00 COMMENT 'Weekly working hours' AFTER employment_type;

-- Verify
SELECT 'Columns added successfully!' AS message;
SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'kita_casa_azul' AND TABLE_NAME = 'admins' AND COLUMN_NAME = 'last_login';
SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'kita_casa_azul' AND TABLE_NAME = 'staff' AND COLUMN_NAME = 'weekly_hours';
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Database columns added successfully!"
    echo ""
    echo "Step 2: Starting backend with Flyway..."
    echo ""
    cd "$(dirname "$0")"
    mvn spring-boot:run
else
    echo ""
    echo "❌ Failed to add columns. Please check your MySQL credentials."
    echo ""
    echo "You can also run the migration manually:"
    echo "  mysql -u root -p kita_casa_azul < add_missing_columns.sql"
    exit 1
fi
