# 🚀 Automated Database Setup Scripts

Three scripts to make database setup effortless!

## 📁 Available Scripts

### 1. **setup-database.sh** ⭐ (Recommended for Linux/macOS)
**Interactive setup with full control**

```bash
chmod +x setup-database.sh
./setup-database.sh
```

**Features:**
- ✅ Auto-detects MySQL access method (sudo, root with/without password)
- ✅ Interactive prompts for custom values
- ✅ Default values for quick setup
- ✅ Full error checking and validation
- ✅ Colored output for easy reading
- ✅ Verification of installed data
- ✅ Complete final instructions

**Best for:**
- First-time setup
- Custom database names/users
- Production environments
- Learning the system

---

### 2. **quick-setup.sh** ⚡ (Fast for Linux/macOS)
**One-command setup with all defaults**

```bash
chmod +x quick-setup.sh
./quick-setup.sh
```

**Features:**
- ✅ Zero questions asked
- ✅ Uses all defaults
- ✅ Completes in ~10 seconds
- ✅ Perfect for development

**Defaults:**
- Database: `kita_casa_azul`
- User: `kita_admin`
- Password: `password123`

**Best for:**
- Quick testing
- Development environment
- CI/CD pipelines
- Rebuilding database

---

### 3. **setup-database.ps1** 🪟 (Windows PowerShell)
**Interactive setup for Windows**

```powershell
# Right-click -> Run with PowerShell
# OR
.\setup-database.ps1
```

**Features:**
- ✅ Full Windows support
- ✅ Interactive configuration
- ✅ Secure password input
- ✅ Colored PowerShell output
- ✅ Same features as Linux version

**Best for:**
- Windows development
- Windows Server deployment

---

## 🎯 Quick Comparison

| Script | OS | Interactive | Speed | Use Case |
|--------|----|-----------:|------:|----------|
| setup-database.sh | Linux/macOS | Yes | Medium | Production, learning |
| quick-setup.sh | Linux/macOS | No | Fast | Development, testing |
| setup-database.ps1 | Windows | Yes | Medium | Windows environments |

---

## 🚀 Quick Start Guide

### Linux/macOS - First Time Setup

```bash
# 1. Make script executable
chmod +x setup-database.sh

# 2. Run it
./setup-database.sh

# 3. Follow prompts (or just press Enter for defaults)

# Done! ✅
```

### Linux/macOS - Quick Development Setup

```bash
# One command, no questions
chmod +x quick-setup.sh && ./quick-setup.sh
```

### Windows - First Time Setup

```powershell
# Run PowerShell as Administrator (optional)
.\setup-database.ps1

# Follow prompts
```

---

## 📋 What These Scripts Do

All scripts perform these steps automatically:

1. ✅ **Check MySQL Installation**
   - Verifies MySQL is installed
   - Detects access method

2. ✅ **Create Database**
   ```sql
   CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
   ```

3. ✅ **Create User**
   ```sql
   CREATE USER 'kita_admin'@'localhost' IDENTIFIED BY 'password123';
   ```

4. ✅ **Grant Permissions**
   ```sql
   GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
   ```

5. ✅ **Load Data**
   - Creates 5 tables
   - Inserts 2 admin users
   - Loads 4 age groups
   - Adds 13 staff members
   - Creates sample schedule (Week 6)

6. ✅ **Verify Setup**
   - Counts records in each table
   - Confirms everything loaded

7. ✅ **Display Instructions**
   - Next steps
   - Login credentials
   - Useful commands

---

## 🔧 Script Requirements

### Linux/macOS Scripts
- ✅ Bash shell
- ✅ MySQL installed
- ✅ MySQL root access (via sudo or password)
- ✅ `init-database.sql` in same directory

### Windows Script
- ✅ PowerShell 5.0+
- ✅ MySQL installed and in PATH
- ✅ MySQL root access
- ✅ `init-database.sql` in same directory

---

## 🎓 Usage Examples

### Example 1: Accept All Defaults

```bash
$ ./setup-database.sh

Step 3: Database configuration
Press Enter to use defaults, or type custom values

Database name [kita_casa_azul]: ⏎
Database user [kita_admin]: ⏎
Database password [password123]: ⏎

✅ Setup complete!
```

### Example 2: Custom Values

```bash
$ ./setup-database.sh

Database name [kita_casa_azul]: my_kita_db
Database user [kita_admin]: my_admin
Database password [password123]: super_secure_123

✅ Setup complete!
```

### Example 3: Quick Setup (No Questions)

```bash
$ ./quick-setup.sh

🚀 Quick Setup - Using defaults...
   Database: kita_casa_azul
   User: kita_admin
   Password: password123

✅ Setup complete!
```

---

## 🔍 Troubleshooting

### Error: "MySQL is not installed"

**Solution:**
```bash
# Ubuntu/Debian
sudo apt-get install mysql-server

# macOS
brew install mysql

# Windows
Download from https://dev.mysql.com/downloads/installer/
```

### Error: "Cannot access MySQL"

**Solution 1:** Try with sudo
```bash
sudo mysql
# If this works, the script will detect it automatically
```

**Solution 2:** Set root password
```bash
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

### Error: "SQL file not found"

**Solution:**
```bash
# Make sure init-database.sql is in the same directory
ls -la init-database.sql

# If not, copy it
cp /path/to/init-database.sql .
```

### Error: "Permission denied"

**Solution:**
```bash
# Make script executable
chmod +x setup-database.sh
chmod +x quick-setup.sh
```

### Windows: "Execution Policy"

**Solution:**
```powershell
# Run as Administrator
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\setup-database.ps1
```

---

## 🔄 Reset Database

If you need to start fresh:

### Option 1: Use Quick Setup
```bash
./quick-setup.sh
# Automatically drops and recreates everything
```

### Option 2: Manual Reset
```bash
mysql -u kita_admin -p kita_casa_azul < init-database.sql
# Reloads all data
```

### Option 3: Complete Wipe
```bash
sudo mysql -e "DROP DATABASE kita_casa_azul;"
./setup-database.sh
```

---

## 📊 What Gets Created

After running any script:

| Item | Count | Details |
|------|-------|---------|
| **Tables** | 5 | admins, weekly_schedules, age_groups, staff, schedule_entries |
| **Admins** | 2 | uwe, alexandre (password: password123) |
| **Age Groups** | 4 | Französische, Minis, Portugiesische, Windelmanagement |
| **Staff** | 13 | 10 Erzieher, 3 Azubi |
| **Schedules** | 1 | Week 6 (Feb 2-6, 2026) |
| **Entries** | 65 | Full week with various statuses |

---

## 🔐 Security Notes

### Default Passwords

⚠️ **IMPORTANT:** All scripts use default password `password123`

**For Production:**

```bash
# After running script, change passwords:
mysql -u root -p

UPDATE admins 
SET password = '$2a$10$YOUR_NEW_BCRYPT_HASH' 
WHERE username = 'uwe';

UPDATE admins 
SET password = '$2a$10$YOUR_NEW_BCRYPT_HASH' 
WHERE username = 'alexandre';
```

**Generate BCrypt hash:** https://bcrypt-generator.com

### Database User

Change `kita_admin` password:

```sql
ALTER USER 'kita_admin'@'localhost' 
IDENTIFIED BY 'your_new_secure_password';
```

Then update `application.properties`:
```properties
spring.datasource.password=your_new_secure_password
```

---

## 💡 Tips & Tricks

### Tip 1: Silent Mode (quick-setup.sh)

```bash
# Redirect output for scripts
./quick-setup.sh > /dev/null 2>&1
echo $?  # 0 = success
```

### Tip 2: Custom SQL File

```bash
# Use different SQL file
export SQL_FILE=/path/to/custom.sql
./setup-database.sh
```

### Tip 3: Backup Before Reset

```bash
# Always backup before resetting
mysqldump -u kita_admin -p kita_casa_azul > backup_$(date +%Y%m%d).sql
./quick-setup.sh
```

### Tip 4: Check What Will Be Created

```bash
# Preview the SQL (Linux/macOS)
less init-database.sql

# Preview the SQL (Windows)
notepad init-database.sql
```

---

## 🎯 Next Steps After Setup

1. **Update Application Config**
   ```bash
   cd backend
   nano src/main/resources/application.properties
   ```
   
   Update:
   ```properties
   spring.datasource.password=password123
   ```

2. **Start Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start Frontend**
   ```bash
   # React
   cd frontend
   npm install
   npm start

   # Angular
   cd frontend
   npm install
   ng serve
   ```

4. **Login**
   - URL: http://localhost:3000 (React) or http://localhost:4200 (Angular)
   - Username: `alexandre` or `uwe`
   - Password: `password123`

---

## 📚 Additional Resources

- **Database Schema:** `init-database.sql`
- **Database Guide:** `DATABASE-README.md`
- **Project README:** `PROJECT-README.md`
- **Quick Start:** `QUICK-START.md`

---

## ❓ FAQ

**Q: Do I need sudo?**  
A: Only for the initial database/user creation. Scripts auto-detect this.

**Q: Can I use different database names?**  
A: Yes! Use `setup-database.sh` (interactive) and enter custom values.

**Q: What if I mess up?**  
A: Just run `./quick-setup.sh` again. It drops and recreates everything.

**Q: Can I run this in Docker?**  
A: Yes! Mount the SQL file and run the script in your MySQL container.

**Q: Does this work with MariaDB?**  
A: Yes! Scripts work with both MySQL and MariaDB.

---

**Version:** 1.0.0  
**Last Updated:** February 2, 2026  
**Compatibility:** MySQL 8.0+, MariaDB 10.5+
