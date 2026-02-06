-- ============================================================================
-- KITA CASA AZUL - Initial Database Schema
-- ============================================================================
-- Flyway Migration V1
-- Description: Creates initial database schema with all tables
-- Author: System
-- Date: 2026-02-06
-- ============================================================================

-- ============================================================================
-- TABLE 1: admins
-- Stores admin users for authentication
-- ============================================================================
CREATE TABLE IF NOT EXISTS admins (
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
CREATE TABLE IF NOT EXISTS weekly_schedules (
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
CREATE TABLE IF NOT EXISTS age_groups (
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
CREATE TABLE IF NOT EXISTS staff (
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
CREATE TABLE IF NOT EXISTS schedule_entries (
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
-- Only insert if table is empty (for first-time setup)
INSERT INTO admins (username, password, full_name, email, is_active, created_by)
SELECT * FROM (
    SELECT 'uwe' as username,
           '$2a$10$xn3LI/AjqicFYZFruSwve.DPUCWpSmEuU9FyRqHpqRbBMTTQnpf4i' as password,
           'Uwe Admin' as full_name,
           'uwe@kitacasaazul.de' as email,
           TRUE as is_active,
           'system' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'uwe');

INSERT INTO admins (username, password, full_name, email, is_active, created_by)
SELECT * FROM (
    SELECT 'alexandre' as username,
           '$2a$10$xn3LI/AjqicFYZFruSwve.DPUCWpSmEuU9FyRqHpqRbBMTTQnpf4i' as password,
           'Alexandre Admin' as full_name,
           'alexandre@kitacasaazul.de' as email,
           TRUE as is_active,
           'system' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'alexandre');

-- ============================================================================
-- SAMPLE DATA: Age Groups
-- ============================================================================
INSERT INTO age_groups (name, description, created_by)
SELECT * FROM (SELECT 'Französische Kindergruppe' as name, 'French kindergarten group for ages 3-6' as description, 'system' as created_by) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM age_groups WHERE name = 'Französische Kindergruppe');

INSERT INTO age_groups (name, description, created_by)
SELECT * FROM (SELECT 'Minis' as name, 'Group for younger children aged 1-3' as description, 'system' as created_by) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM age_groups WHERE name = 'Minis');

INSERT INTO age_groups (name, description, created_by)
SELECT * FROM (SELECT 'Portugiesische Kindergruppe' as name, 'Portuguese kindergarten group for ages 3-6' as description, 'system' as created_by) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM age_groups WHERE name = 'Portugiesische Kindergruppe');

INSERT INTO age_groups (name, description, created_by)
SELECT * FROM (SELECT 'Windelmanagement' as name, 'Diaper management for French and Portuguese groups' as description, 'system' as created_by) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM age_groups WHERE name = 'Windelmanagement');
