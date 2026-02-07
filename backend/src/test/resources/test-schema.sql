-- ============================================================================
-- H2 Test Database Schema
-- ============================================================================
-- Simplified schema for H2 in-memory database testing
-- Based on Flyway V1__Initial_Schema.sql
-- ============================================================================

-- Drop tables in reverse dependency order (for test cleanup)
DROP TABLE IF EXISTS schedule_entries;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS age_groups;
DROP TABLE IF EXISTS weekly_schedules;
DROP TABLE IF EXISTS admins;

-- ============================================================================
-- TABLE 1: admins
-- ============================================================================
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admins_username ON admins(username);
CREATE INDEX idx_admins_active ON admins(is_active);

-- ============================================================================
-- TABLE 2: weekly_schedules
-- ============================================================================
CREATE TABLE weekly_schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    week_number INT NOT NULL,
    `YEAR` INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    notes TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_week UNIQUE (week_number, `YEAR`)
);

CREATE INDEX idx_weekly_schedules_dates ON weekly_schedules(start_date, end_date);

-- ============================================================================
-- TABLE 3: age_groups
-- ============================================================================
CREATE TABLE age_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLE 4: staff
-- ============================================================================
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(100) NOT NULL,
    group_id BIGINT,
    employment_type VARCHAR(50),
    weekly_hours DECIMAL(5,2) DEFAULT 40.00,
    email VARCHAR(255),
    phone VARCHAR(50),
    is_praktikant BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    hire_date DATE,
    termination_date DATE,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_group FOREIGN KEY (group_id) REFERENCES age_groups(id) ON DELETE SET NULL
);

CREATE INDEX idx_staff_group ON staff(group_id);
CREATE INDEX idx_staff_active ON staff(is_active);
CREATE INDEX idx_staff_role ON staff(role);

-- ============================================================================
-- TABLE 5: schedule_entries
-- ============================================================================
CREATE TABLE schedule_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    weekly_schedule_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedule_weekly FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE,
    CONSTRAINT fk_schedule_staff FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,
    CONSTRAINT unique_schedule_entry UNIQUE (weekly_schedule_id, staff_id, day_of_week)
);

CREATE INDEX idx_schedule_entries_week ON schedule_entries(weekly_schedule_id);
CREATE INDEX idx_schedule_entries_staff ON schedule_entries(staff_id);
CREATE INDEX idx_schedule_entries_date ON schedule_entries(work_date);
CREATE INDEX idx_schedule_entries_status ON schedule_entries(status);
