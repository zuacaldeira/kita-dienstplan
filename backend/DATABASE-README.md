# 🗄️ Kita Casa Azul - Database Setup

**Single-File Database Initialization**

## 📁 Files

### Main File (Use This!)
- **`init-database.sql`** ⭐ - Complete database setup (all-in-one)
  - Schema with all tables
  - Admin users (with BCrypt passwords)
  - Sample data (Week 6, 13 staff, 4 age groups)
  - Audit tracking columns
  - ~400 lines

### Legacy Files (For Reference)
- `mysql-schema.sql` - Schema only
- `mysql-sample-data.sql` - Sample data only  
- `security-migration.sql` - Security additions only
- `migration-groups-to-age-groups.sql` - Migration script

## 🚀 Quick Start (2 minutes)

### Option A: Fresh Database

```bash
# 1. Create database and user
sudo mysql -u root -p
```

```sql
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
CREATE USER 'kita_admin'@'localhost' IDENTIFIED BY 'your_secure_password';
GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

```bash
# 2. Run initialization script
mysql -u kita_admin -p kita_casa_azul < init-database.sql

# ✅ Done! Database ready!
```

### Option B: Using Root

```bash
mysql -u root -p < init-database.sql
```

## 📊 What Gets Created

### Tables (5):
1. **`admins`** - Admin users for authentication
2. **`weekly_schedules`** - Week definitions
3. **`age_groups`** - Kindergarten age groups
4. **`staff`** - Staff members
5. **`schedule_entries`** - Individual shifts

### Default Data:

**Admins (2):**
- Username: `uwe` / Password: `password123`
- Username: `alexandre` / Password: `password123`

**Age Groups (4):**
- Französische Kindergruppe
- Minis
- Portugiesische Kindergruppe
- Windelmanagement

**Staff (13):**
- 10 Erzieher (educators)
- 3 Azubi (trainees, marked as Praktikant)

**Schedule:**
- Week 6 (Feb 2-6, 2026)
- 65 schedule entries
- Various statuses: normal, frei, krank, Schule, Urlaub

## ✅ Verification

After running the script:

```bash
mysql -u kita_admin -p kita_casa_azul
```

```sql
-- Check tables
SHOW TABLES;

-- Check admin users
SELECT username, full_name FROM admins;

-- Check age groups
SELECT * FROM age_groups;

-- Check staff count
SELECT COUNT(*) FROM staff;

-- Check schedule entries
SELECT COUNT(*) FROM schedule_entries;
```

Expected results:
- 5 tables
- 2 admins
- 4 age groups
- 13 staff
- 65 schedule entries

## 🔄 Reset Database

To start fresh:

```bash
mysql -u kita_admin -p kita_casa_azul
```

```sql
DROP DATABASE kita_casa_azul;
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
EXIT;
```

```bash
mysql -u kita_admin -p kita_casa_azul < init-database.sql
```

## 📝 Application Configuration

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kita_casa_azul
spring.datasource.username=kita_admin
spring.datasource.password=your_secure_password

# JWT Configuration
jwt.secret=your-very-long-secret-key-min-256-bits
jwt.expiration=86400000
```

## 🔐 Security Notes

**⚠️ IMPORTANT:**

1. **Change Default Passwords!**
   - Default admin password is `password123`
   - Generate BCrypt hash: https://bcrypt-generator.com
   - Update in database:
   ```sql
   UPDATE admins SET password = '$2a$10$YOUR_NEW_HASH' WHERE username = 'uwe';
   ```

2. **JWT Secret:**
   - Generate secure random key (256+ bits)
   - Never commit to version control
   - Use environment variable in production

3. **Database User:**
   - Use strong password for `kita_admin`
   - Limit privileges in production
   - Consider separate read-only user for reporting

## 📐 Schema Highlights

### Audit Tracking
All tables include:
- `created_by` - Username who created record
- `updated_by` - Username who last updated
- `created_at` - Timestamp created
- `updated_at` - Auto-updating timestamp

### Features
- ✅ Foreign key constraints
- ✅ Cascade deletes where appropriate
- ✅ Unique constraints
- ✅ Indexes for performance
- ✅ UTF8MB4 (full Unicode support)
- ✅ InnoDB engine (ACID compliance)

### Status Values
Schedule entry `status` can be:
- `normal` - Regular working day
- `frei` - Day off
- `krank` - Sick leave
- `Schule` - School day (for Azubi)
- `Fachschule` - Vocational school
- `Urlaub` - Vacation

## 🎯 Next Steps

1. ✅ Run `init-database.sql`
2. ✅ Update `application.properties`
3. ✅ Start Spring Boot: `mvn spring-boot:run`
4. ✅ Start Frontend: `npm start` or `ng serve`
5. ✅ Login with default credentials
6. ⚠️ Change admin passwords!

## 📚 Additional Resources

- **Backend README:** `PROJECT-README.md`
- **Security Guide:** `SECURITY-GUIDE.md`
- **Quick Start:** `QUICK-START.md`

---

**Database Version:** 1.0.0  
**Last Updated:** February 2, 2026  
**MySQL Version:** 8.0+
