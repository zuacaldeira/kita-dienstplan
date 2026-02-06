#!/bin/bash
# ============================================================================
# Quick Database Setup - Uses all defaults, no questions asked
# ============================================================================

set -e

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

DB_NAME="kita_casa_azul"
DB_USER="kita_admin"
DB_PASS="Unicidade17!/"

echo "🚀 Quick Setup - Using defaults..."
echo "   Database: $DB_NAME"
echo "   User: $DB_USER"
echo "   Password: $DB_PASS"
echo ""

# Check if MySQL is accessible
if sudo mysql -e "SELECT 1;" &> /dev/null; then
    echo "✅ MySQL accessible with sudo"
    
    # Create database and user
    sudo mysql << EOF
CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP USER IF EXISTS '${DB_USER}'@'localhost';
CREATE USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';
GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF
    
    echo "✅ Database and user created"
    
    # Load data
    mysql -u ${DB_USER} -p${DB_PASS} ${DB_NAME} < init-database.sql 2>/dev/null
    
    echo "✅ Data loaded"
    echo ""
    echo -e "${GREEN}✅ Setup complete!${NC}"
    echo ""
    echo "Login: mysql -u ${DB_USER} -p ${DB_NAME}"
    echo "Password: ${DB_PASS}"
    echo ""
    echo "Admin users: alexandre / password123"
    echo "             uwe / password123"
    
else
    echo -e "${RED}❌ Cannot access MySQL with sudo${NC}"
    echo "Please run: ./setup-database.sh (interactive version)"
    exit 1
fi
