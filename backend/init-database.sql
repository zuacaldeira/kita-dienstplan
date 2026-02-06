-- ============================================================================
-- KITA CASA AZUL - COMPLETE DATABASE SETUP
-- ============================================================================
-- Description: Complete database schema with sample data
-- Version: 1.0
-- Date: February 2, 2026
-- ============================================================================
-- USAGE:
--   mysql -u kita_admin -p kita_casa_azul < kita-complete-setup.sql
-- ============================================================================

-- ============================================================================
-- CLEANUP: Drop existing tables (in reverse dependency order)
-- ============================================================================
DROP TABLE IF EXISTS schedule_entries;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS age_groups;
DROP TABLE IF EXISTS weekly_schedules;
DROP TABLE IF EXISTS admins;

-- ============================================================================
-- TABLE 1: admins
-- Stores admin users for authentication
-- ============================================================================
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt hashed password',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL COMMENT 'Last successful login timestamp',
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admins_username (username),
    INDEX idx_admins_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 2: weekly_schedules
-- Stores information about each week's schedule
-- ============================================================================
CREATE TABLE weekly_schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    week_number INT NOT NULL,
    year INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    notes TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_week (week_number, year),
    INDEX idx_weekly_schedules_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 3: age_groups
-- Stores kindergarten age groups
-- ============================================================================
CREATE TABLE age_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 4: staff
-- Stores all personnel information
-- ============================================================================
CREATE TABLE staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(100) NOT NULL,
    group_id INT,
    employment_type VARCHAR(50),
    weekly_hours DECIMAL(5,2) DEFAULT 40.00 COMMENT 'Weekly working hours',
    email VARCHAR(255),
    phone VARCHAR(50),
    is_praktikant BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    hire_date DATE,
    termination_date DATE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES age_groups(id) ON DELETE SET NULL,
    INDEX idx_staff_group (group_id),
    INDEX idx_staff_active (is_active),
    INDEX idx_staff_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE 5: schedule_entries
-- Stores individual shift entries for each staff member
-- ============================================================================
CREATE TABLE schedule_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    weekly_schedule_id INT NOT NULL,
    staff_id INT NOT NULL,
    day_of_week INT NOT NULL,
    work_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    status VARCHAR(50) DEFAULT 'normal',
    working_hours_minutes INT DEFAULT 0,
    break_minutes INT DEFAULT 0,
    notes TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    UNIQUE KEY unique_schedule_entry (weekly_schedule_id, staff_id, day_of_week),
    INDEX idx_schedule_entries_week (weekly_schedule_id),
    INDEX idx_schedule_entries_staff (staff_id),
    INDEX idx_schedule_entries_date (work_date),
    INDEX idx_schedule_entries_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- SAMPLE DATA: Admin Users
-- ============================================================================
-- Password for both: password123
-- BCrypt hash: $2a$10$xn3LI/AjqicFYZFruSwve.DPUCWpSmEuU9FyRqHpqRbBMTTQnpf4i

INSERT INTO admins (username, password, full_name, email, is_active, created_by) VALUES
('uwe', '$2a$10$xn3LI/AjqicFYZFruSwve.DPUCWpSmEuU9FyRqHpqRbBMTTQnpf4i', 'Uwe Admin', 'uwe@kitacasaazul.de', TRUE, 'system'),
('alexandre', '$2a$10$xn3LI/AjqicFYZFruSwve.DPUCWpSmEuU9FyRqHpqRbBMTTQnpf4i', 'Alexandre Admin', 'alexandre@kitacasaazul.de', TRUE, 'system');

-- ============================================================================
-- SAMPLE DATA: Age Groups
-- ============================================================================
INSERT INTO age_groups (name, description, created_by) VALUES
('Französische Kindergruppe', 'French kindergarten group for ages 3-6', 'system'),
('Minis', 'Group for younger children aged 1-3', 'system'),
('Portugiesische Kindergruppe', 'Portuguese kindergarten group for ages 3-6', 'system'),
('Windelmanagement', 'Diaper management for French and Portuguese groups', 'system');

-- ============================================================================
-- SAMPLE DATA: Weekly Schedule (Week 6, 2026)
-- ============================================================================
INSERT INTO weekly_schedules (week_number, year, start_date, end_date, notes, created_by) VALUES
(6, 2026, '2026-02-02', '2026-02-06', 'Regular week schedule', 'system');

-- ============================================================================
-- SAMPLE DATA: Staff Members
-- ============================================================================
-- Französische Kindergruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Hans', 'Müller', 'Hans Müller', 'Erzieher', ag.id, 'Vollzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Französische Kindergruppe';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Anna', 'Schmidt', 'Anna Schmidt', 'Erzieherin', ag.id, 'Vollzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Französische Kindergruppe';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Lisa', 'Weber', 'Lisa Weber', 'Azubi', ag.id, 'Vollzeit', TRUE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Französische Kindergruppe';

-- Minis
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Maria', 'Koch', 'Maria Koch', 'Erzieherin', ag.id, 'Vollzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Minis';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Peter', 'Fischer', 'Peter Fischer', 'Erzieher', ag.id, 'Teilzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Minis';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Julia', 'Wagner', 'Julia Wagner', 'Azubi', ag.id, 'Vollzeit', TRUE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Minis';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Sarah', 'Becker', 'Sarah Becker', 'Praktikantin', ag.id, 'Vollzeit', TRUE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Minis';

-- Portugiesische Kindergruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Carlos', 'Silva', 'Carlos Silva', 'Erzieher', ag.id, 'Vollzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Portugiesische Kindergruppe';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Emma', 'Hoffmann', 'Emma Hoffmann', 'Erzieherin', ag.id, 'Vollzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Portugiesische Kindergruppe';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Laura', 'Richter', 'Laura Richter', 'Azubi', ag.id, 'Vollzeit', TRUE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Portugiesische Kindergruppe';

-- Windelmanagement
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Sophie', 'Klein', 'Sophie Klein', 'Erzieherin', ag.id, 'Teilzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Windelmanagement';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Max', 'Braun', 'Max Braun', 'Erzieher', ag.id, 'Teilzeit', FALSE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Windelmanagement';

INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, is_praktikant, is_active, created_by)
SELECT 'Nina', 'Zimmermann', 'Nina Zimmermann', 'Praktikantin', ag.id, 'Vollzeit', TRUE, TRUE, 'system'
FROM age_groups ag WHERE ag.name = 'Windelmanagement';

-- ============================================================================
-- SAMPLE DATA: Schedule Entries (Week 6 - Monday samples)
-- ============================================================================
INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, working_hours_minutes, break_minutes, created_by)
SELECT 1, s.id, 0, '2026-02-02', '08:00:00', '16:00:00', 'normal', 450, 30, 'system'
FROM staff s WHERE s.full_name = 'Hans Müller';

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, working_hours_minutes, break_minutes, created_by)
SELECT 1, s.id, 0, '2026-02-02', '08:30:00', '16:30:00', 'normal', 450, 30, 'system'
FROM staff s WHERE s.full_name = 'Anna Schmidt';

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, working_hours_minutes, break_minutes, created_by)
SELECT 1, s.id, 0, '2026-02-02', '09:00:00', '17:00:00', 'normal', 450, 30, 'system'
FROM staff s WHERE s.full_name = 'Maria Koch';

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, working_hours_minutes, break_minutes, created_by)
SELECT 1, s.id, 0, '2026-02-02', '08:00:00', '13:00:00', 'normal', 270, 30, 'system'
FROM staff s WHERE s.full_name = 'Peter Fischer';

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status, working_hours_minutes, break_minutes, created_by)
SELECT 1, s.id, 0, '2026-02-02', '08:00:00', '16:00:00', 'normal', 450, 30, 'system'
FROM staff s WHERE s.full_name = 'Carlos Silva';

-- ============================================================================
-- VERIFICATION
-- ============================================================================
SELECT '============================================================' AS '';
SELECT 'DATABASE SETUP COMPLETED!' AS '';
SELECT '============================================================' AS '';
SELECT '' AS '';
SELECT CONCAT('✓ Admins created: ', COUNT(*)) as result FROM admins;
SELECT CONCAT('✓ Age groups created: ', COUNT(*)) as result FROM age_groups;
SELECT CONCAT('✓ Staff members created: ', COUNT(*)) as result FROM staff;
SELECT CONCAT('✓ Weekly schedules created: ', COUNT(*)) as result FROM weekly_schedules;
SELECT CONCAT('✓ Schedule entries created: ', COUNT(*)) as result FROM schedule_entries;
SELECT '' AS '';
SELECT 'Login credentials:' AS '';
SELECT '  Username: uwe or alexandre' AS '';
SELECT '  Password: password123' AS '';
SELECT '============================================================' AS '';
