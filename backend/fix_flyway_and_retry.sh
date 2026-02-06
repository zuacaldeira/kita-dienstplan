#!/bin/bash
# ============================================================================
# Fix Flyway Failed Migration and Retry
# ============================================================================

echo "============================================================"
echo "Fixing Flyway Migration Failure"
echo "============================================================"
echo ""
echo "This script will:"
echo "1. Remove the failed V2 migration from Flyway history"
echo "2. Restart the backend to retry the migration"
echo ""
echo "Please enter your MySQL root password when prompted:"
echo ""

mysql -u root -p kita_casa_azul << 'EOF'
-- Remove failed migration from Flyway history
DELETE FROM flyway_schema_history WHERE version = '2' AND success = 0;

-- Show current migration status
SELECT 'Flyway migration history after cleanup:' AS message;
SELECT installed_rank, version, description, type, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
EOF

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Flyway history cleaned up successfully!"
    echo ""
    echo "Starting backend to retry V2 migration..."
    echo ""
    cd "$(dirname "$0")"
    mvn spring-boot:run
else
    echo ""
    echo "❌ Failed to clean up Flyway history"
    echo "Please check your MySQL credentials"
    exit 1
fi
