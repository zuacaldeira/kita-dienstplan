# Legacy SQL Files

These files are kept for reference and advanced use cases.

## 📁 Files in This Folder

### `mysql-schema.sql` (12 KB)
- **What:** Database schema only (CREATE TABLE statements)
- **Use when:** You want to create tables but add your own data
- **Includes:** All 5 tables with audit columns

### `mysql-sample-data.sql` (31 KB)
- **What:** Sample data only (INSERT statements)
- **Use when:** You already have schema and want to add sample data
- **Includes:** 2 admins, 4 age groups, 13 staff, 65 schedule entries

### `security-migration.sql` (4.7 KB)
- **What:** Security features only (admins table + audit columns)
- **Use when:** You have old database without security and want to add it
- **Includes:** Admin users, audit columns migration

### `migration-groups-to-age-groups.sql` (3.6 KB)
- **What:** Migration script to rename `groups` → `age_groups`
- **Use when:** You have existing database with old table name
- **Includes:** Verification queries and rollback procedure

## 🚀 Recommended Approach

**For New Projects:**
Use the main file instead:
```bash
mysql -u kita_admin -p kita_casa_azul < ../init-database.sql
```

## 📝 Advanced Usage

### Separate Schema and Data

If you prefer separate files:

```bash
# 1. Create schema only
mysql -u kita_admin -p kita_casa_azul < mysql-schema.sql

# 2. Add your own data or use sample data
mysql -u kita_admin -p kita_casa_azul < mysql-sample-data.sql
```

### Custom Data with Standard Schema

```bash
# 1. Create schema
mysql -u kita_admin -p kita_casa_azul < mysql-schema.sql

# 2. Add only admin users
mysql -u kita_admin -p kita_casa_azul < security-migration.sql

# 3. Add your own age groups, staff, schedules...
```

### Migrate Existing Database

If you have a database with the old `groups` table name:

```bash
mysql -u kita_admin -p kita_casa_azul < migration-groups-to-age-groups.sql
```

## 🔄 File Relationships

```
init-database.sql (⭐ USE THIS!)
    ├── Contains: mysql-schema.sql
    ├── Contains: security-migration.sql (admins)
    └── Contains: mysql-sample-data.sql

--- OR ---

Manual approach:
    1. mysql-schema.sql (tables)
    2. security-migration.sql (admins + audit)
    3. mysql-sample-data.sql (sample data)
```

## ⚠️ Important Notes

1. **These files use `age_groups` table name** (not `groups`)
2. All files assume UTF8MB4 charset
3. Security files include BCrypt password hashes
4. Sample data is for Week 6, 2026 (Feb 2-6)

## 💡 When to Use These Files

**Use legacy files when:**
- You want fine-grained control over setup steps
- You need to debug specific parts
- You're integrating with existing data
- You're creating custom deployment scripts

**Use main file when:**
- Setting up new development environment
- Quick testing
- Standard deployment
- Learning the system

---

**Tip:** The main `init-database.sql` file is recommended for 99% of use cases. These legacy files are here for special situations and reference.
