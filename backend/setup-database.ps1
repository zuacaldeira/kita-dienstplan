# ============================================================================
# Kita Casa Azul - Database Setup Script for Windows
# ============================================================================
# Requirements:
#   - MySQL installed and in PATH
#   - Run as Administrator (or have MySQL root access)
# ============================================================================

# Configuration
$DBName = "kita_casa_azul"
$DBUser = "kita_admin"
$DBPass = "password123"
$DBHost = "localhost"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$SQLFile = Join-Path $ScriptDir "init-database.sql"

# Colors
function Write-Success {
    Write-Host "✅ $args" -ForegroundColor Green
}

function Write-Error {
    Write-Host "❌ $args" -ForegroundColor Red
}

function Write-Info {
    Write-Host "ℹ️  $args" -ForegroundColor Cyan
}

function Write-Warning {
    Write-Host "⚠️  $args" -ForegroundColor Yellow
}

# Header
Write-Host ""
Write-Host "============================================================================" -ForegroundColor Blue
Write-Host "  🏫 Kita Casa Azul - Database Setup (Windows)" -ForegroundColor Blue
Write-Host "============================================================================" -ForegroundColor Blue
Write-Host ""

# Check MySQL
Write-Info "Checking MySQL installation..."
try {
    $null = & mysql --version 2>&1
    Write-Success "MySQL is installed"
} catch {
    Write-Error "MySQL not found in PATH"
    Write-Host "Please install MySQL or add it to your PATH"
    exit 1
}

# Check SQL file
if (-not (Test-Path $SQLFile)) {
    Write-Error "SQL file not found: $SQLFile"
    Write-Host "Make sure init-database.sql is in the same directory"
    exit 1
}
Write-Success "SQL file found"

Write-Host ""

# Get MySQL root password
Write-Info "MySQL root access required..."
$RootPass = Read-Host "Enter MySQL root password (press Enter if none)" -AsSecureString
$RootPassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($RootPass)
)

Write-Host ""

# Custom values (optional)
Write-Info "Press Enter to use defaults, or type custom values"
Write-Host ""

$InputDB = Read-Host "Database name [$DBName]"
if ($InputDB) { $DBName = $InputDB }

$InputUser = Read-Host "Database user [$DBUser]"
if ($InputUser) { $DBUser = $InputUser }

$InputPass = Read-Host "Database password [$DBPass]" -AsSecureString
$InputPassPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($InputPass)
)
if ($InputPassPlain) { $DBPass = $InputPassPlain }

Write-Host ""

# Create database and user
Write-Info "Creating database and user..."

$CreateSQL = @"
CREATE DATABASE IF NOT EXISTS ``$DBName`` 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

DROP USER IF EXISTS '$DBUser'@'$DBHost';
CREATE USER '$DBUser'@'$DBHost' 
    IDENTIFIED BY '$DBPass';

GRANT ALL PRIVILEGES ON ``$DBName``.* 
    TO '$DBUser'@'$DBHost';

FLUSH PRIVILEGES;
"@

try {
    if ($RootPassPlain) {
        $CreateSQL | & mysql -u root -p"$RootPassPlain" 2>&1 | Out-Null
    } else {
        $CreateSQL | & mysql -u root 2>&1 | Out-Null
    }
    Write-Success "Database '$DBName' created"
    Write-Success "User '$DBUser' created"
} catch {
    Write-Error "Failed to create database or user"
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host ""

# Load data
Write-Info "Loading schema and sample data..."

try {
    Get-Content $SQLFile | & mysql -u $DBUser -p"$DBPass" $DBName 2>&1 | Out-Null
    Write-Success "Database initialized successfully"
} catch {
    Write-Error "Failed to load data"
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host ""

# Verify
Write-Info "Verifying setup..."

$VerifySQL = @"
SELECT 
    (SELECT COUNT(*) FROM admins) as admins,
    (SELECT COUNT(*) FROM age_groups) as age_groups,
    (SELECT COUNT(*) FROM staff) as staff,
    (SELECT COUNT(*) FROM schedule_entries) as entries;
"@

try {
    $result = $VerifySQL | & mysql -u $DBUser -p"$DBPass" $DBName -N 2>&1
    $counts = $result -split "`t"
    
    Write-Host ""
    Write-Host "Database Contents:"
    Write-Host "  📋 Admins: $($counts[0])"
    Write-Host "  🏢 Age Groups: $($counts[1])"
    Write-Host "  👥 Staff: $($counts[2])"
    Write-Host "  📅 Schedule Entries: $($counts[3])"
    Write-Host ""
    
    if ($counts[0] -eq 2 -and $counts[1] -eq 4 -and $counts[2] -eq 13) {
        Write-Success "All data loaded correctly!"
    }
} catch {
    Write-Warning "Could not verify data"
}

Write-Host ""

# Final instructions
Write-Host "============================================================================" -ForegroundColor Green
Write-Host "  ✅ DATABASE SETUP COMPLETE!" -ForegroundColor Green
Write-Host "============================================================================" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Database Details:"
Write-Host "  Database: $DBName"
Write-Host "  User: $DBUser"
Write-Host "  Password: $DBPass"
Write-Host "  Host: $DBHost"
Write-Host ""
Write-Host "🔑 Default Admin Credentials:"
Write-Host "  Username: alexandre  |  Password: password123"
Write-Host "  Username: uwe        |  Password: password123"
Write-Host ""
Write-Warning "IMPORTANT: Change these passwords in production!"
Write-Host ""
Write-Host "🚀 Next Steps:"
Write-Host "  1. Update application.properties with:"
Write-Host "     spring.datasource.password=$DBPass"
Write-Host ""
Write-Host "  2. Start backend: mvn spring-boot:run"
Write-Host ""
Write-Host "  3. Start frontend: npm start or ng serve"
Write-Host ""
Write-Host "💡 Useful Commands:"
Write-Host "  mysql -u $DBUser -p $DBName"
Write-Host ""
Write-Host "============================================================================"
Write-Host ""
