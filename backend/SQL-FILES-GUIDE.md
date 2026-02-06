# 📁 SQL Files Guide - Kita Casa Azul

## 🎯 Simplified SQL Setup

The project now has **2 consolidated SQL files** instead of 3-4 separate files.

---

## 📄 Available SQL Files

### 1. **kita-complete-setup.sql** ⭐ RECOMMENDED
**What it includes:**
- ✅ Complete database schema (5 tables)
- ✅ Security (admins table with audit columns)
- ✅ Sample data (4 groups, 13 staff, Week 6 schedule)
- ✅ 2 admin users (uwe, alexandre)

**Use this if:**
- You want to test the system immediately
- You want sample data to explore features
- You're doing development/testing

**Size:** ~12 KB  
**Tables:** 5 (admins, weekly_schedules, age_groups, staff, schedule_entries)  
**Sample Records:** ~60 total

```bash
mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql
```

---

### 2. **kita-schema-only.sql**
**What it includes:**
- ✅ Complete database schema (5 tables)
- ✅ Security (admins table with audit columns)
- ✅ 2 admin users (required for login)
- ❌ NO sample data

**Use this if:**
- You want to start with empty tables
- You're deploying to production
- You want to add your own data

**Size:** ~4 KB  
**Tables:** 5 (same as above)  
**Sample Records:** Only 2 admin users

```bash
mysql -u kita_admin -p kita_casa_azul < kita-schema-only.sql
```

---

## 🚀 Quick Setup

### Option 1: With Sample Data (Recommended for Testing)

```bash
# 1. Create database
mysql -u root -p
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4;
CREATE USER 'kita_admin'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
EXIT;

# 2. Run complete setup
mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql

# 3. Verify
mysql -u kita_admin -p kita_casa_azul -e "SHOW TABLES;"
mysql -u kita_admin -p kita_casa_azul -e "SELECT COUNT(*) FROM staff;"
```

### Option 2: Schema Only (Production)

```bash
# Same step 1 as above, then:

# 2. Run schema only
mysql -u kita_admin -p kita_casa_azul < kita-schema-only.sql

# 3. Add your own data through the application
```

---

## 📊 What's Included

### Database Schema (Both Files):

```
┌─────────────────────────────────────────────────────┐
│  admins              - Admin users (with BCrypt)    │
│  weekly_schedules    - Week definitions             │
│  age_groups          - Kindergarten groups          │
│  staff               - Employee records             │
│  schedule_entries    - Individual shifts            │
└─────────────────────────────────────────────────────┘
```

### Sample Data (Complete Setup Only):

**Admins:** 2 users
- `uwe` / `password123`
- `alexandre` / `password123`

**Age Groups:** 4 groups
- Französische Kindergruppe
- Minis
- Portugiesische Kindergruppe
- Windelmanagement

**Staff:** 13 employees
- 6 Erzieherinnen
- 3 Azubis
- 2 Praktikantinnen
- Mix across all 4 groups

**Schedule:** Week 6 (Feb 2-6, 2026)
- ~40 schedule entries
- Monday through Friday
- 8:00-16:00 shifts

---

## 🗑️ Old Files (Deprecated)

The following files are **no longer needed** and can be ignored:

- ❌ `mysql-schema.sql` - Replaced by consolidated files
- ❌ `mysql-sample-data.sql` - Included in complete setup
- ❌ `security-migration.sql` - Security now built-in
- ❌ `migration-groups-to-age-groups.sql` - Already applied

These are kept for reference but **don't use them**.

---

## 🔧 Features Included

### Security:
- ✅ BCrypt password hashing
- ✅ JWT-ready admin table
- ✅ Audit columns (created_by, updated_by)
- ✅ Timestamps (created_at, updated_at)

### Database Design:
- ✅ Foreign key constraints
- ✅ Cascading deletes
- ✅ Unique constraints
- ✅ Proper indexes
- ✅ UTF8MB4 character set

### Best Practices:
- ✅ InnoDB engine
- ✅ Optimized indexes
- ✅ Data integrity
- ✅ Clean structure

---

## 📝 Column Reference

### Common Audit Columns (All Tables):
```sql
created_by VARCHAR(50)          -- Who created (auto-populated)
updated_by VARCHAR(50)          -- Who last updated (auto-populated)
created_at TIMESTAMP            -- When created (auto)
updated_at TIMESTAMP            -- When last updated (auto)
```

### Table-Specific Columns:

**admins:**
- username, password (BCrypt), full_name, email, is_active

**weekly_schedules:**
- week_number, year, start_date, end_date, notes

**age_groups:**
- name, description, is_active

**staff:**
- first_name, last_name, full_name, role, group_id
- employment_type, email, phone
- is_praktikant, is_active, hire_date, termination_date

**schedule_entries:**
- weekly_schedule_id, staff_id, day_of_week, work_date
- start_time, end_time, status
- working_hours_minutes, break_minutes, notes

---

## 🎯 Comparison

| Feature | Complete Setup | Schema Only |
|---------|---------------|-------------|
| Tables | 5 | 5 |
| Admins | 2 | 2 |
| Age Groups | 4 | 0 |
| Staff | 13 | 0 |
| Schedules | 1 week | 0 |
| Entries | ~40 | 0 |
| **Size** | **~12 KB** | **~4 KB** |
| **Use Case** | Testing/Dev | Production |

---

## ✅ Verification

After running either SQL file:

```sql
-- Check tables
SHOW TABLES;

-- Check counts
SELECT 'Admins' AS table_name, COUNT(*) AS count FROM admins
UNION ALL SELECT 'Age Groups', COUNT(*) FROM age_groups
UNION ALL SELECT 'Staff', COUNT(*) FROM staff
UNION ALL SELECT 'Schedules', COUNT(*) FROM weekly_schedules
UNION ALL SELECT 'Entries', COUNT(*) FROM schedule_entries;

-- Test login credentials
SELECT username, full_name FROM admins WHERE is_active = TRUE;
```

**Expected output (Complete Setup):**
```
Admins: 2
Age Groups: 4
Staff: 13
Schedules: 1
Entries: ~40
```

**Expected output (Schema Only):**
```
Admins: 2
Age Groups: 0
Staff: 0
Schedules: 0
Entries: 0
```

---

## 🚨 Important Notes

1. **Both files include admin users** - Required for JWT authentication
2. **Default password is `password123`** - Change in production!
3. **Use Schema Only for production** - Add real data through app
4. **Complete Setup is for testing** - Has realistic sample data
5. **UTF8MB4 required** - Supports all Unicode characters

---

## 🔄 Migration from Old Files

If you previously used the 3 separate files:

**Old way:**
```bash
mysql ... < mysql-schema.sql
mysql ... < mysql-sample-data.sql
mysql ... < security-migration.sql
```

**New way:**
```bash
mysql ... < kita-complete-setup.sql
```

Same result, much simpler! ✅

---

## 📖 Next Steps

1. ✅ Choose your SQL file (complete or schema-only)
2. ✅ Run the SQL file
3. ✅ Configure `application.properties`
4. ✅ Start Spring Boot backend
5. ✅ Login with: alexandre / password123
6. ✅ Start using the system!

---

**Files:** 2 consolidated SQL files  
**Setup Time:** 2 minutes  
**Status:** ✅ Production Ready
