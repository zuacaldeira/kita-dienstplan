-- ============================================================================
-- KITA CASA AZUL - DATABASE SCHEMA (Production)
-- ============================================================================
-- Description: Database schema without sample data - for production use
-- Version: 1.0
-- Date: February 2, 2026
-- ============================================================================
-- USAGE:
--   mysql -u kita_admin -p kita_casa_azul < kita-schema-only.sql
--
-- ⚠️  IMPORTANT: After running this, create admin users using the app
--                or insert manually with BCrypt hashed passwords
-- ============================================================================

DROP TABLE IF EXISTS schedule_entries;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS age_groups;
DROP TABLE IF EXISTS weekly_schedules;
DROP TABLE IF EXISTS admins;

-- ============================================================================
CREATE TABLE admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL COMMENT 'BCrypt hashed',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admins_username (username),
    INDEX idx_admins_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
CREATE TABLE staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(100) NOT NULL,
    group_id INT,
    employment_type VARCHAR(50),
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
SELECT 'Schema created! Create admin users before starting the application.' AS status;
