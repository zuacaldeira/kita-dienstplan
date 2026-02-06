# Flyway Database Migrations

This directory contains Flyway database migration scripts for the Kita Casa Azul application.

## What is Flyway?

Flyway is a database migration tool that:
- Tracks which migrations have been applied
- Applies new migrations automatically on application startup
- Ensures database schema consistency across environments
- Provides version control for database changes

## How It Works

1. **On Application Startup**: Flyway checks the `flyway_schema_history` table to see which migrations have been applied
2. **Applies Pending Migrations**: Any new migration scripts are executed in order
3. **Records Success**: Successfully applied migrations are recorded in the history table

## Migration Naming Convention

Migration files must follow this pattern:

```
V{version}__{description}.sql
```

Examples:
- `V1__Initial_Schema.sql`
- `V2__Add_Last_Login_Column.sql`
- `V3__Add_Weekly_Hours_Column.sql`
- `V1.1__Add_User_Preferences.sql`

**Important Rules:**
- Start with `V` (capital V)
- Version number (can be numeric or semantic: V1, V2, V1.1, V2.3.1)
- Two underscores `__` separate version from description
- Description uses underscores instead of spaces
- File extension must be `.sql`

## Creating a New Migration

### Step 1: Create the Migration File

Create a new file in this directory following the naming convention:

```bash
touch V3__Add_Employee_Number_Column.sql
```

### Step 2: Write the Migration SQL

```sql
-- ============================================================================
-- MIGRATION: Add Employee Number to Staff
-- ============================================================================
-- Version: V3
-- Description: Adds employee_number column to staff table for HR tracking
-- Date: 2026-02-07
-- ============================================================================

ALTER TABLE staff
ADD COLUMN employee_number VARCHAR(50) UNIQUE
COMMENT 'Unique employee identification number'
AFTER full_name;

-- Create index for faster lookups
CREATE INDEX idx_staff_employee_number ON staff(employee_number);
```

### Step 3: Test the Migration

1. **Restart the application**: The migration will be applied automatically
2. **Check the logs**: Look for Flyway migration messages
3. **Verify in database**:
   ```sql
   SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;
   ```

## Migration Best Practices

### 1. Never Modify Applied Migrations
Once a migration has been applied (especially in production), **never modify it**. Always create a new migration.

❌ **Wrong:**
```sql
-- Modifying V2__Add_Email_Column.sql after it's been applied
ALTER TABLE users ADD COLUMN email VARCHAR(255);
ALTER TABLE users ADD COLUMN phone VARCHAR(50); -- Added later
```

✅ **Correct:**
```sql
-- V2__Add_Email_Column.sql (original, don't touch)
ALTER TABLE users ADD COLUMN email VARCHAR(255);

-- V3__Add_Phone_Column.sql (new migration)
ALTER TABLE users ADD COLUMN phone VARCHAR(50);
```

### 2. Make Migrations Idempotent When Possible

Use `IF NOT EXISTS` and `IF EXISTS` to make migrations safer:

```sql
-- Safe to run multiple times
ALTER TABLE staff
ADD COLUMN IF NOT EXISTS phone VARCHAR(50);

-- Check before dropping
DROP INDEX IF EXISTS idx_old_index ON staff;
```

### 3. Include Rollback Instructions (Optional)

While Flyway doesn't support automatic rollback, document the rollback in comments:

```sql
-- ============================================================================
-- MIGRATION: Add Employee Number
-- ============================================================================

ALTER TABLE staff ADD COLUMN employee_number VARCHAR(50);

-- ============================================================================
-- ROLLBACK (manual):
-- ALTER TABLE staff DROP COLUMN employee_number;
-- ============================================================================
```

### 4. Test Before Committing

1. Test on local database first
2. Verify the migration succeeds
3. Check application functionality
4. Review the schema changes

### 5. Keep Migrations Small and Focused

One logical change per migration makes troubleshooting easier.

❌ **Too much in one migration:**
```sql
V5__Multiple_Changes.sql -- adds columns, modifies tables, inserts data
```

✅ **Split into focused migrations:**
```sql
V5__Add_User_Preferences_Table.sql
V6__Add_Status_Column_To_Staff.sql
V7__Seed_Initial_Preferences.sql
```

## Common Migration Patterns

### Adding a Column
```sql
ALTER TABLE table_name
ADD COLUMN column_name VARCHAR(100) DEFAULT 'default_value'
COMMENT 'Column description';
```

### Adding an Index
```sql
CREATE INDEX idx_table_column ON table_name(column_name);
```

### Adding a Foreign Key
```sql
ALTER TABLE child_table
ADD CONSTRAINT fk_child_parent
FOREIGN KEY (parent_id) REFERENCES parent_table(id)
ON DELETE CASCADE;
```

### Modifying a Column
```sql
ALTER TABLE table_name
MODIFY COLUMN column_name VARCHAR(200) NOT NULL;
```

### Inserting Reference Data
```sql
INSERT INTO statuses (code, name, description)
VALUES
  ('ACTIVE', 'Active', 'User is active'),
  ('INACTIVE', 'Inactive', 'User is inactive')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description);
```

## Checking Migration Status

### View Applied Migrations
```sql
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC;
```

### View Flyway Configuration
Check `application.properties`:
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

## Troubleshooting

### Migration Failed
1. **Check the error message** in application logs
2. **Fix the SQL** in the migration file
3. **Manually fix the database** if needed
4. **Update Flyway history**:
   ```sql
   DELETE FROM flyway_schema_history WHERE version = 'X' AND success = 0;
   ```
5. **Restart the application** to retry

### Skip a Migration (Emergency Only)
If you need to skip a failed migration temporarily:
```sql
INSERT INTO flyway_schema_history (version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
VALUES ('X', 'Description', 'SQL', 'VX__Description.sql', NULL, 'manual', NOW(), 0, 1);
```

### Baseline Existing Database
If you're adding Flyway to an existing database:
```properties
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

This is already configured in `application.properties`.

## Example: Complete Migration Workflow

### Scenario: Add email notifications feature

```bash
# 1. Create migration file
touch V4__Add_Email_Notifications.sql
```

```sql
-- V4__Add_Email_Notifications.sql
CREATE TABLE email_notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    body TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add notification preferences to admins
ALTER TABLE admins
ADD COLUMN email_notifications_enabled BOOLEAN DEFAULT TRUE
AFTER email;
```

```bash
# 2. Restart application
mvn spring-boot:run

# 3. Verify in logs
# [Flyway] Successfully applied 1 migration to schema `kita_casa_azul`, now at version v4

# 4. Commit to Git
git add src/main/resources/db/migration/V4__Add_Email_Notifications.sql
git commit -m "Add email notifications feature - database migration"
```

## Current Migrations

- **V1__Initial_Schema.sql**: Creates all base tables (admins, staff, age_groups, weekly_schedules, schedule_entries) with sample data

## References

- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Flyway Spring Boot Integration](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)
