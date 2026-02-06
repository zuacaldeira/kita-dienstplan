# Flyway Database Migration - Setup Complete ✅

## What Was Done

Flyway has been successfully configured for the Kita Casa Azul project. Database schema changes are now managed through versioned migration scripts instead of manual SQL execution.

### Changes Made

1. **Added Flyway Dependencies** (`pom.xml`)
   - `flyway-core` - Main Flyway library
   - `flyway-mysql` - MySQL-specific Flyway support

2. **Configured Flyway** (`application.properties`)
   - Enabled Flyway migrations
   - Set migration location: `classpath:db/migration`
   - Enabled baseline-on-migrate for existing databases
   - Configured migration validation

3. **Created Migration Structure**
   - `src/main/resources/db/migration/` - Migration scripts directory
   - `V1__Initial_Schema.sql` - Baseline migration with complete schema
   - `README.md` - Comprehensive migration guide

4. **Fixed Database Issues**
   - Added `last_login` column to `admins` table
   - Added `weekly_hours` column to `staff` table
   - Fixed boolean field handling in Admin, Staff, and Group entities

## How to Use (First Time Setup)

### Option 1: Start Fresh Database

If you want to recreate the database from scratch:

```bash
# 1. Drop and recreate database
mysql -u root -p << 'EOF'
DROP DATABASE IF EXISTS kita_casa_azul;
CREATE DATABASE kita_casa_azul CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
GRANT ALL PRIVILEGES ON kita_casa_azul.* TO 'kita_admin'@'localhost';
FLUSH PRIVILEGES;
EOF

# 2. Start the backend - Flyway will create all tables automatically
cd backend
mvn spring-boot:run
```

Flyway will:
- Create the `flyway_schema_history` table
- Apply `V1__Initial_Schema.sql`
- Create all tables (admins, staff, age_groups, weekly_schedules, schedule_entries)
- Insert sample data (admins and age groups)

### Option 2: Migrate Existing Database

If you have an existing database with the old schema:

```bash
# 1. Backup your database first!
mysqldump -u root -p kita_casa_azul > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. Start the backend - Flyway will baseline and skip V1
cd backend
mvn spring-boot:run
```

Flyway will:
- Detect existing tables
- Create `flyway_schema_history` table
- Mark V1 as already applied (baseline)
- Your data remains intact

**Note**: If your existing database is missing `last_login` or `weekly_hours` columns, you'll need to add them manually:

```sql
mysql -u root -p kita_casa_azul << 'EOF'
ALTER TABLE admins ADD COLUMN IF NOT EXISTS last_login TIMESTAMP NULL COMMENT 'Last successful login timestamp' AFTER is_active;
ALTER TABLE staff ADD COLUMN IF NOT EXISTS weekly_hours DECIMAL(5,2) DEFAULT 40.00 COMMENT 'Weekly working hours' AFTER employment_type;
EOF
```

## Verifying Flyway Setup

### Check Migration Status

After starting the application, verify Flyway ran successfully:

```sql
mysql -u root -p kita_casa_azul

-- View migration history
SELECT * FROM flyway_schema_history;

-- Expected output:
-- installed_rank | version | description      | type | script                 | checksum   | installed_on        | success
-- 1              | 1       | Initial Schema   | SQL  | V1__Initial_Schema.sql | -123456789 | 2026-02-06 20:30:00 | 1
```

### Check Application Logs

Look for Flyway messages in the startup logs:

```
INFO  org.flywaydb.core.internal.command.DbValidate - Successfully validated 1 migration
INFO  org.flywaydb.core.internal.command.DbMigrate - Current version of schema `kita_casa_azul`: 1
INFO  org.flywaydb.core.internal.command.DbMigrate - Schema `kita_casa_azul` is up to date. No migration necessary.
```

## Creating Your First Migration

Let's say you want to add a `notes` field to the admins table:

### Step 1: Create Migration File

```bash
cd backend/src/main/resources/db/migration
touch V2__Add_Notes_To_Admins.sql
```

### Step 2: Write Migration SQL

```sql
-- V2__Add_Notes_To_Admins.sql
-- ============================================================================
-- MIGRATION: Add Notes Field to Admins
-- ============================================================================
-- Description: Allows admins to store personal notes or preferences
-- Date: 2026-02-06
-- ============================================================================

ALTER TABLE admins
ADD COLUMN notes TEXT NULL
COMMENT 'Personal notes or preferences for admin user'
AFTER email;

-- Create index if searching notes will be needed
-- CREATE FULLTEXT INDEX idx_admins_notes ON admins(notes);
```

### Step 3: Apply Migration

Just restart the application:

```bash
mvn spring-boot:run
```

Flyway will automatically detect and apply V2. You'll see:

```
INFO  org.flywaydb.core.internal.command.DbMigrate - Migrating schema `kita_casa_azul` to version "2 - Add Notes To Admins"
INFO  org.flywaydb.core.internal.command.DbMigrate - Successfully applied 1 migration to schema `kita_casa_azul`, now at version v2
```

### Step 4: Verify

```sql
-- Check the new column exists
DESCRIBE admins;

-- Check migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC;
```

## Common Workflows

### Adding a New Column

```sql
-- V3__Add_Phone_To_Groups.sql
ALTER TABLE age_groups
ADD COLUMN contact_phone VARCHAR(50) NULL
COMMENT 'Contact phone number for age group coordinator'
AFTER description;
```

### Creating a New Table

```sql
-- V4__Create_Holidays_Table.sql
CREATE TABLE holidays (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    is_recurring BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_holiday_date (date),
    INDEX idx_holidays_date (date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Modifying a Column

```sql
-- V5__Increase_Email_Length.sql
ALTER TABLE admins
MODIFY COLUMN email VARCHAR(320) NULL
COMMENT 'Email address (RFC 5321 max length)';
```

### Adding an Index

```sql
-- V6__Add_Index_Staff_Name.sql
CREATE INDEX idx_staff_fullname ON staff(full_name);
```

### Inserting Reference Data

```sql
-- V7__Add_Default_Roles.sql
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO roles (code, name, description) VALUES
('ERZIEHER', 'Erzieher/in', 'Qualified kindergarten teacher'),
('KINDERPFLEGER', 'Kinderpfleger/in', 'Child care worker'),
('PRAKTIKANT', 'Praktikant/in', 'Intern'),
('LEITUNG', 'Leitung', 'Director/Manager');
```

## Migration Best Practices

### ✅ DO

- **Test locally first** before committing
- **Keep migrations small** - one logical change per migration
- **Use descriptive names** - `V2__Add_Email_Notifications.sql` not `V2__Changes.sql`
- **Add comments** explaining why the change is needed
- **Review before committing** - migrations can't be easily changed once applied
- **Commit migrations with code changes** that depend on them

### ❌ DON'T

- **Never modify applied migrations** - create a new one instead
- **Don't skip version numbers** - V1, V2, V3 (not V1, V3, V5)
- **Don't use dynamic SQL** - keep migrations simple and reproducible
- **Don't put too much in one migration** - break complex changes into steps

## Troubleshooting

### Migration Failed with Error

```bash
# 1. Check the error in logs
# 2. Fix the SQL syntax in the migration file
# 3. Manually fix the database if needed
# 4. Mark migration as failed and retry:

mysql -u root -p kita_casa_azul << 'EOF'
DELETE FROM flyway_schema_history WHERE version = 'X' AND success = 0;
EOF

# 5. Restart application to retry
mvn spring-boot:run
```

### Want to Skip a Migration

```bash
# Generally not recommended, but if needed:
mysql -u root -p kita_casa_azul << 'EOF'
INSERT INTO flyway_schema_history (version, description, type, script, installed_by, installed_on, execution_time, success)
VALUES ('X', 'Migration description', 'SQL', 'VX__Description.sql', 'manual', NOW(), 0, 1);
EOF
```

### Check Migration Checksum

If you accidentally modified a migration and need to repair:

```bash
# This is dangerous - only do in development!
DELETE FROM flyway_schema_history WHERE version = 'X';
# Then restart to reapply
```

## Disabling Flyway (If Needed)

If you ever need to disable Flyway temporarily:

```properties
# In application.properties
spring.flyway.enabled=false
```

## Benefits of Using Flyway

✅ **Version Control**: Database schema is versioned like code
✅ **Reproducible**: Same migrations apply the same changes everywhere
✅ **Team Collaboration**: Everyone gets schema changes automatically
✅ **Rollback Documentation**: Each migration can document its rollback
✅ **Environment Parity**: Dev, staging, and prod stay in sync
✅ **Audit Trail**: `flyway_schema_history` shows who changed what and when
✅ **Automated**: No manual SQL execution needed

## Resources

- **Full Documentation**: See `src/main/resources/db/migration/README.md`
- **Flyway Docs**: https://flywaydb.org/documentation/
- **Spring Boot Integration**: https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway

## Quick Reference Card

| Task | Command |
|------|---------|
| Create migration | `touch V{N}__{Description}.sql` |
| Apply migrations | `mvn spring-boot:run` |
| Check status | `SELECT * FROM flyway_schema_history;` |
| Test migration | Restart app and check logs |
| Rollback (manual) | Write reverse SQL and execute |

---

**You're all set! 🚀** Database migrations are now managed through Flyway. Create new migrations in `src/main/resources/db/migration/` and they'll be applied automatically on startup.
