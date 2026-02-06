# 📁 SQL Files - Setup Guide

## 🎯 Simplified Structure

The SQL files have been **merged into a minimal set** for easier setup:

```
kita-spring-api/
├── kita-complete-setup.sql   ⭐ RECOMMENDED for development
└── kita-schema-only.sql       🏭 For production
```

---

## 📋 File Descriptions

### 1. ⭐ `kita-complete-setup.sql` (RECOMMENDED)

**Use this for:** Development, Testing, Demo

**What it does:**
- ✅ Creates all 5 tables with audit columns
- ✅ Includes 2 admin users (uwe, alexandre)
- ✅ Adds 4 age groups
- ✅ Adds 13 staff members
- ✅ Adds 1 weekly schedule (Week 6)
- ✅ Adds sample schedule entries

**Size:** ~280 lines

**Usage:**
```bash
mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql
```

**Default Login:**
- Username: `uwe` or `alexandre`
- Password: `password123`

---

### 2. 🏭 `kita-schema-only.sql`

**Use this for:** Production deployment

**What it does:**
- ✅ Creates all 5 tables with audit columns
- ⚠️ No sample data
- ⚠️ No admin users (must create manually)

**Size:** ~120 lines

**Usage:**
```bash
mysql -u kita_admin -p kita_casa_azul < kita-schema-only.sql
```

**After running:**
You must create admin users before starting the application.

---

## 🗄️ Tables Created

Both files create these 5 tables:

1. **admins** - Admin users with BCrypt passwords
2. **weekly_schedules** - Week definitions
3. **age_groups** - Kindergarten age groups (renamed from `groups`)
4. **staff** - Employee information
5. **schedule_entries** - Individual shift entries

**All tables include:**
- ✅ Audit columns (created_by, updated_by)
- ✅ Timestamps (created_at, updated_at)
- ✅ Proper indexes
- ✅ Foreign key constraints
- ✅ UTF8MB4 charset

---

## 🚀 Quick Start

### Option 1: Complete Setup (Recommended)

```bash
# 1. Create database
sudo mysql -u root -p
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
CREATE USER 'kita_admin'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
EXIT;

# 2. Run complete setup
mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql

# 3. Done! Ready to use
# Login: alexandre / password123
```

### Option 2: Production Setup

```bash
# 1. Create database (same as above)

# 2. Run schema only
mysql -u kita_admin -p kita_casa_azul < kita-schema-only.sql

# 3. Create admin user manually
mysql -u kita_admin -p kita_casa_azul
INSERT INTO admins (username, password, full_name, email, created_by)
VALUES ('admin', '$2a$10$YOUR_BCRYPT_HASH', 'Admin User', 'admin@example.com', 'system');
```

---

## 🔑 Password Hashing

Passwords must be BCrypt hashed. Use online tools or:

**Java:**
```java
BCrypt.hashpw("your_password", BCrypt.gensalt(10))
```

**Python:**
```python
import bcrypt
bcrypt.hashpw(b'your_password', bcrypt.gensalt())
```

**Online:**
- https://bcrypt-generator.com/
- Use rounds: 10

---

## 📊 Sample Data Summary

**kita-complete-setup.sql** includes:

| Table | Records |
|-------|---------|
| admins | 2 |
| age_groups | 4 |
| staff | 13 |
| weekly_schedules | 1 |
| schedule_entries | 5 (Monday samples) |

**Groups:**
1. Französische Kindergruppe
2. Minis
3. Portugiesische Kindergruppe
4. Windelmanagement

---

## 🔄 What Changed?

### Old Structure (4 files):
```
❌ mysql-schema.sql
❌ mysql-sample-data.sql
❌ security-migration.sql
❌ migration-groups-to-age-groups.sql
```

### New Structure (2 files):
```
✅ kita-complete-setup.sql (schema + data)
✅ kita-schema-only.sql (production)
```

**Benefits:**
- 📉 75% fewer files
- ⚡ Faster setup
- 🎯 Clearer purpose
- 🔧 No migrations needed
- ✅ age_groups from start

---

## ✅ Verification

After running the SQL file:

```sql
-- Check tables
SHOW TABLES;

-- Check data (complete setup only)
SELECT COUNT(*) FROM admins;      -- Should return 2
SELECT COUNT(*) FROM age_groups;  -- Should return 4
SELECT COUNT(*) FROM staff;       -- Should return 13

-- Test login
SELECT * FROM admins WHERE username = 'alexandre';

-- Check foreign keys
SELECT 
    s.full_name,
    ag.name as age_group
FROM staff s
LEFT JOIN age_groups ag ON s.group_id = ag.id
LIMIT 5;
```

---

## 🎯 Which File Should I Use?

| Scenario | Use |
|----------|-----|
| Development | ⭐ kita-complete-setup.sql |
| Testing | ⭐ kita-complete-setup.sql |
| Demo | ⭐ kita-complete-setup.sql |
| Production | 🏭 kita-schema-only.sql |
| Learning | ⭐ kita-complete-setup.sql |

**99% of users should use `kita-complete-setup.sql`** ⭐

---

## 🔧 Troubleshooting

### "Table already exists"
```bash
# Drop and recreate
mysql -u kita_admin -p kita_casa_azul
DROP DATABASE kita_casa_azul;
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
EXIT;

# Then run setup again
mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql
```

### "Access denied"
Check database user permissions:
```sql
SHOW GRANTS FOR 'kita_admin'@'localhost';
```

### "Unknown database"
Create the database first:
```sql
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
```

---

## 📚 Additional Info

- **Charset:** UTF8MB4 (full Unicode support)
- **Collation:** utf8mb4_unicode_ci
- **Engine:** InnoDB (ACID compliant)
- **Foreign Keys:** ON DELETE CASCADE/SET NULL
- **Indexes:** Optimized for common queries

---

## 🎉 Summary

**Simple 2-file structure:**
1. Use `kita-complete-setup.sql` for everything except production
2. Use `kita-schema-only.sql` for production
3. Both create the exact same schema
4. Complete setup adds sample data for testing

**No migrations, no complications, just run one file!** 🚀

---

**Version:** 1.0  
**Last Updated:** February 2, 2026  
**Status:** ✅ Production Ready
