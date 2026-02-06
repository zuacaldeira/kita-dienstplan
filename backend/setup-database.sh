#!/bin/bash
# ============================================================================
# Kita Casa Azul - Automated Database Setup Script
# ============================================================================
# This script will:
#   1. Create MySQL database
#   2. Create MySQL user
#   3. Grant permissions
#   4. Load schema and sample data
#   5. Verify everything works
# ============================================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default values
DB_NAME="kita_casa_azul"
DB_USER="kita_admin"
DB_PASS="password123"
DB_HOST="localhost"

# Script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
SQL_FILE="${SCRIPT_DIR}/init-database.sql"

# ============================================================================
# Functions
# ============================================================================

print_header() {
    echo -e "${BLUE}"
    echo "============================================================================"
    echo "  🏫 Kita Casa Azul - Database Setup"
    echo "============================================================================"
    echo -e "${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

check_mysql_installed() {
    if ! command -v mysql &> /dev/null; then
        print_error "MySQL is not installed!"
        echo "Install MySQL first:"
        echo "  Ubuntu/Debian: sudo apt-get install mysql-server"
        echo "  macOS: brew install mysql"
        exit 1
    fi
    print_success "MySQL is installed"
}

check_sql_file() {
    if [ ! -f "$SQL_FILE" ]; then
        print_error "SQL file not found: $SQL_FILE"
        echo "Make sure init-database.sql is in the same directory as this script"
        exit 1
    fi
    print_success "SQL file found: $(basename $SQL_FILE)"
}

detect_mysql_access() {
    print_info "Detecting MySQL access method..."
    
    # Try without password first (socket authentication)
    if sudo mysql -e "SELECT 1;" &> /dev/null; then
        MYSQL_ACCESS="sudo"
        print_success "Using sudo mysql (socket authentication)"
        return 0
    fi
    
    # Try root with password
    echo -n "Enter MySQL root password (press Enter if none): "
    read -s ROOT_PASS
    echo
    
    if [ -z "$ROOT_PASS" ]; then
        if mysql -u root -e "SELECT 1;" &> /dev/null; then
            MYSQL_ACCESS="root_no_pass"
            print_success "Using mysql -u root (no password)"
            return 0
        fi
    else
        if mysql -u root -p"$ROOT_PASS" -e "SELECT 1;" &> /dev/null; then
            MYSQL_ACCESS="root_with_pass"
            print_success "Using mysql -u root with password"
            return 0
        fi
    fi
    
    print_error "Cannot access MySQL. Please check your MySQL installation."
    exit 1
}

execute_mysql_root() {
    local sql="$1"
    
    case "$MYSQL_ACCESS" in
        "sudo")
            echo "$sql" | sudo mysql
            ;;
        "root_no_pass")
            echo "$sql" | mysql -u root
            ;;
        "root_with_pass")
            echo "$sql" | mysql -u root -p"$ROOT_PASS"
            ;;
    esac
}

create_database_and_user() {
    print_info "Creating database and user..."
    
    local sql="
-- Create database
CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\` 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

-- Create user (drop if exists to avoid errors)
DROP USER IF EXISTS '${DB_USER}'@'${DB_HOST}';
CREATE USER '${DB_USER}'@'${DB_HOST}' 
    IDENTIFIED BY '${DB_PASS}';

-- Grant all privileges on the database
GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* 
    TO '${DB_USER}'@'${DB_HOST}';

-- Apply changes
FLUSH PRIVILEGES;

-- Verify
SELECT 'Database and user created successfully!' AS Status;
"
    
    if execute_mysql_root "$sql"; then
        print_success "Database '${DB_NAME}' created"
        print_success "User '${DB_USER}' created with full privileges"
    else
        print_error "Failed to create database or user"
        exit 1
    fi
}

load_initial_data() {
    print_info "Loading database schema and sample data..."
    
    if mysql -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" < "${SQL_FILE}" 2>&1 | grep -v "Using a password"; then
        print_success "Database initialized successfully"
    else
        print_error "Failed to load initial data"
        exit 1
    fi
}

verify_setup() {
    print_info "Verifying database setup..."
    
    local verify_sql="
SELECT 
    (SELECT COUNT(*) FROM admins) as admins,
    (SELECT COUNT(*) FROM age_groups) as age_groups,
    (SELECT COUNT(*) FROM staff) as staff,
    (SELECT COUNT(*) FROM schedule_entries) as entries;
"
    
    echo "$verify_sql" | mysql -u "${DB_USER}" -p"${DB_PASS}" "${DB_NAME}" 2>/dev/null | tail -n +2 > /tmp/kita_verify.txt
    
    read admins age_groups staff entries < /tmp/kita_verify.txt
    
    echo ""
    echo "Database Contents:"
    echo "  📋 Admins: $admins"
    echo "  🏢 Age Groups: $age_groups"
    echo "  👥 Staff: $staff"
    echo "  📅 Schedule Entries: $entries"
    echo ""
    
    if [ "$admins" -eq 2 ] && [ "$age_groups" -eq 4 ] && [ "$staff" -eq 13 ] && [ "$entries" -gt 0 ]; then
        print_success "All data loaded correctly!"
        return 0
    else
        print_warning "Data counts don't match expected values"
        return 1
    fi
}

print_final_instructions() {
    echo ""
    echo -e "${GREEN}"
    echo "============================================================================"
    echo "  ✅ DATABASE SETUP COMPLETE!"
    echo "============================================================================"
    echo -e "${NC}"
    echo ""
    echo "📊 Database Details:"
    echo "  Database: ${DB_NAME}"
    echo "  User: ${DB_USER}"
    echo "  Password: ${DB_PASS}"
    echo "  Host: ${DB_HOST}"
    echo ""
    echo "🔑 Default Admin Credentials:"
    echo "  Username: alexandre  |  Password: password123"
    echo "  Username: uwe        |  Password: password123"
    echo ""
    echo -e "${YELLOW}⚠️  IMPORTANT: Change these passwords in production!${NC}"
    echo ""
    echo "🚀 Next Steps:"
    echo "  1. Update backend/src/main/resources/application.properties:"
    echo "     spring.datasource.password=${DB_PASS}"
    echo ""
    echo "  2. Start Spring Boot backend:"
    echo "     cd backend && mvn spring-boot:run"
    echo ""
    echo "  3. Start frontend (choose one):"
    echo "     cd frontend && npm start         (React - port 3000)"
    echo "     cd frontend && ng serve          (Angular - port 4200)"
    echo ""
    echo "  4. Login at http://localhost:3000 or http://localhost:4200"
    echo ""
    echo "💡 Useful Commands:"
    echo "  Login to MySQL:"
    echo "    mysql -u ${DB_USER} -p ${DB_NAME}"
    echo ""
    echo "  Backup database:"
    echo "    mysqldump -u ${DB_USER} -p ${DB_NAME} > backup.sql"
    echo ""
    echo "  Reset database:"
    echo "    mysql -u ${DB_USER} -p ${DB_NAME} < init-database.sql"
    echo ""
    echo "============================================================================"
}

# ============================================================================
# Main Script
# ============================================================================

main() {
    print_header
    
    # Step 1: Check prerequisites
    echo "Step 1: Checking prerequisites..."
    check_mysql_installed
    check_sql_file
    echo ""
    
    # Step 2: Detect MySQL access
    echo "Step 2: Detecting MySQL access method..."
    detect_mysql_access
    echo ""
    
    # Step 3: Ask for custom values (optional)
    echo "Step 3: Database configuration"
    print_info "Press Enter to use defaults, or type custom values"
    echo ""
    
    read -p "Database name [${DB_NAME}]: " input
    DB_NAME=${input:-$DB_NAME}
    
    read -p "Database user [${DB_USER}]: " input
    DB_USER=${input:-$DB_USER}
    
    read -sp "Database password [${DB_PASS}]: " input
    echo
    DB_PASS=${input:-$DB_PASS}
    echo ""
    
    # Step 4: Create database and user
    echo "Step 4: Creating database and user..."
    create_database_and_user
    echo ""
    
    # Step 5: Load initial data
    echo "Step 5: Loading schema and sample data..."
    load_initial_data
    echo ""
    
    # Step 6: Verify setup
    echo "Step 6: Verifying setup..."
    verify_setup
    echo ""
    
    # Step 7: Print final instructions
    print_final_instructions
}

# Run main function
main

exit 0
