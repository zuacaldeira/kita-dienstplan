-- ============================================================================
-- KITA CASA AZUL - DIENSTPLAN DATABASE SCHEMA (MySQL)
-- ============================================================================

-- Drop tables if they exist (in reverse order of dependencies)
DROP TABLE IF EXISTS schedule_entries;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS age_groups;
DROP TABLE IF EXISTS weekly_schedules;

-- ============================================================================
-- TABLE: weekly_schedules
-- Stores information about each week's schedule
-- ============================================================================
CREATE TABLE weekly_schedules (
    id INT AUTO_INCREMENT PRIMARY KEY,
    week_number INT NOT NULL,
    year INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    notes TEXT,

    -- Ensure we don't have duplicate weeks
    UNIQUE KEY unique_week (week_number, year),

    -- Index for faster queries by date
    INDEX idx_weekly_schedules_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: age_groups
-- Stores kindergarten age groups (Französische, Minis, Portugiesische, etc.)
-- ============================================================================
CREATE TABLE age_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: staff
-- Stores all personnel information
-- ============================================================================
CREATE TABLE staff (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    full_name VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(100) NOT NULL,  -- Erzieher, Azubi, Praktikant, etc.
    group_id INT,
    employment_type VARCHAR(50),  -- full-time (fr.), part-time (pt.)
    email VARCHAR(255),
    phone VARCHAR(50),
    is_praktikant BOOLEAN DEFAULT FALSE,  -- For excluding from totals
    is_active BOOLEAN DEFAULT TRUE,
    hire_date DATE,
    termination_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign key to age_groups
    FOREIGN KEY (group_id) REFERENCES age_groups(id) ON DELETE SET NULL,

    -- Indexes for faster queries
    INDEX idx_staff_group (group_id),
    INDEX idx_staff_active (is_active),
    INDEX idx_staff_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- TABLE: schedule_entries
-- Stores individual shift entries for each staff member, day, and week
-- ============================================================================
CREATE TABLE schedule_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    weekly_schedule_id INT NOT NULL,
    staff_id INT NOT NULL,
    day_of_week INT NOT NULL,  -- 0=Monday, 6=Sunday
    work_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    status VARCHAR(50) DEFAULT 'normal',  -- normal, frei, krank, Schule, Fachschule, Urlaub
    working_hours_minutes INT DEFAULT 0,  -- Stored in minutes for easier calculations
    break_minutes INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Foreign keys
    FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE,

    -- Ensure one entry per staff per day per week
    UNIQUE KEY unique_schedule_entry (weekly_schedule_id, staff_id, day_of_week),

    -- Indexes for faster queries
    INDEX idx_schedule_entries_weekly (weekly_schedule_id),
    INDEX idx_schedule_entries_staff (staff_id),
    INDEX idx_schedule_entries_date (work_date),
    INDEX idx_schedule_entries_status (status),

    -- Check constraints (MySQL 8.0.16+)
    CHECK (day_of_week BETWEEN 0 AND 6),
    CHECK (working_hours_minutes >= 0),
    CHECK (break_minutes >= 0),
    CHECK (status IN ('normal', 'frei', 'krank', 'Schule', 'Fachschule', 'Urlaub', 'Feiertag'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- VIEWS FOR COMMON QUERIES
-- ============================================================================

-- View: Daily totals with and without Praktikanten
CREATE OR REPLACE VIEW daily_totals AS
SELECT
    se.weekly_schedule_id,
    se.work_date,
    se.day_of_week,
    -- Total without Praktikanten
    SUM(CASE WHEN s.is_praktikant = FALSE THEN COALESCE(se.working_hours_minutes, 0) ELSE 0 END) as total_minutes_without_praktikanten,
    -- Total with Praktikanten
    SUM(COALESCE(se.working_hours_minutes, 0)) as total_minutes_with_praktikanten,
    -- Count of staff
    SUM(CASE WHEN s.is_praktikant = FALSE THEN 1 ELSE 0 END) as staff_count_without_praktikanten,
    COUNT(*) as total_staff_count
FROM schedule_entries se
JOIN staff s ON se.staff_id = s.id
GROUP BY se.weekly_schedule_id, se.work_date, se.day_of_week;

-- View: Weekly totals per staff member
CREATE OR REPLACE VIEW weekly_staff_totals AS
SELECT
    se.weekly_schedule_id,
    se.staff_id,
    s.full_name,
    s.role,
    g.name as group_name,
    SUM(COALESCE(se.working_hours_minutes, 0)) as total_working_minutes,
    SUM(COALESCE(se.break_minutes, 0)) as total_break_minutes,
    SUM(CASE WHEN se.status = 'normal' THEN 1 ELSE 0 END) as days_worked,
    SUM(CASE WHEN se.status = 'krank' THEN 1 ELSE 0 END) as days_sick,
    SUM(CASE WHEN se.status = 'frei' THEN 1 ELSE 0 END) as days_off,
    SUM(CASE WHEN se.status IN ('Schule', 'Fachschule') THEN 1 ELSE 0 END) as school_days
FROM schedule_entries se
JOIN staff s ON se.staff_id = s.id
LEFT JOIN groups g ON s.group_id = g.id
GROUP BY se.weekly_schedule_id, se.staff_id, s.full_name, s.role, g.name;

-- View: Complete schedule with all details
CREATE OR REPLACE VIEW schedule_detail AS
SELECT
    ws.week_number,
    ws.year,
    ws.start_date,
    g.name as group_name,
    s.full_name as staff_name,
    s.role,
    se.day_of_week,
    se.work_date,
    se.start_time,
    se.end_time,
    se.status,
    se.working_hours_minutes,
    se.break_minutes,
    -- Formatted times
    CONCAT(LPAD(FLOOR(se.working_hours_minutes / 60), 2, '0'), ':',
           LPAD(MOD(se.working_hours_minutes, 60), 2, '0')) as working_hours_formatted,
    CONCAT(LPAD(FLOOR(se.break_minutes / 60), 2, '0'), ':',
           LPAD(MOD(se.break_minutes, 60), 2, '0')) as break_time_formatted
FROM schedule_entries se
JOIN weekly_schedules ws ON se.weekly_schedule_id = ws.id
JOIN staff s ON se.staff_id = s.id
LEFT JOIN groups g ON s.group_id = g.id
ORDER BY ws.week_number, g.name, s.full_name, se.day_of_week;

-- ============================================================================
-- STORED PROCEDURES AND TRIGGERS
-- ============================================================================

DELIMITER //

-- Procedure: Calculate working hours and break from start/end times
CREATE PROCEDURE calculate_working_hours(
    IN p_start_time TIME,
    IN p_end_time TIME,
    IN p_status VARCHAR(50),
    OUT p_working_minutes INT,
    OUT p_break_minutes INT
)
BEGIN
    DECLARE total_minutes INT;
    DECLARE work_hours DECIMAL(10,2);

    -- If status is not normal, return 0
    IF p_status != 'normal' OR p_start_time IS NULL OR p_end_time IS NULL THEN
        SET p_working_minutes = 0;
        SET p_break_minutes = 0;
    ELSE
        -- Calculate total minutes
        SET total_minutes = TIMESTAMPDIFF(MINUTE, p_start_time, p_end_time);

        -- Handle overnight shifts (if end < start)
        IF total_minutes < 0 THEN
            SET total_minutes = total_minutes + (24 * 60);
        END IF;

        -- Calculate break (30 minutes if > 6 hours)
        SET work_hours = total_minutes / 60.0;
        IF work_hours > 6 THEN
            SET p_break_minutes = 30;
        ELSE
            SET p_break_minutes = 0;
        END IF;

        -- Return working minutes (total - break)
        SET p_working_minutes = total_minutes - p_break_minutes;
    END IF;
END //

-- Trigger: Auto-calculate working hours on insert
CREATE TRIGGER before_schedule_entry_insert
BEFORE INSERT ON schedule_entries
FOR EACH ROW
BEGIN
    DECLARE calc_work_minutes INT;
    DECLARE calc_break_minutes INT;

    CALL calculate_working_hours(
        NEW.start_time,
        NEW.end_time,
        NEW.status,
        calc_work_minutes,
        calc_break_minutes
    );

    SET NEW.working_hours_minutes = calc_work_minutes;
    SET NEW.break_minutes = calc_break_minutes;
END //

-- Trigger: Auto-calculate working hours on update
CREATE TRIGGER before_schedule_entry_update
BEFORE UPDATE ON schedule_entries
FOR EACH ROW
BEGIN
    DECLARE calc_work_minutes INT;
    DECLARE calc_break_minutes INT;

    -- Only recalculate if time or status changed
    IF NEW.start_time != OLD.start_time
       OR NEW.end_time != OLD.end_time
       OR NEW.status != OLD.status THEN

        CALL calculate_working_hours(
            NEW.start_time,
            NEW.end_time,
            NEW.status,
            calc_work_minutes,
            calc_break_minutes
        );

        SET NEW.working_hours_minutes = calc_work_minutes;
        SET NEW.break_minutes = calc_break_minutes;
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- HELPER FUNCTIONS
-- ============================================================================

DELIMITER //

-- Function to format minutes as H:MM
CREATE FUNCTION format_minutes_to_time(minutes INT)
RETURNS VARCHAR(10)
DETERMINISTIC
BEGIN
    RETURN CONCAT(LPAD(FLOOR(minutes / 60), 2, '0'), ':',
                  LPAD(MOD(minutes, 60), 2, '0'));
END //

-- Function to get day name in German
CREATE FUNCTION get_day_name_de(day_num INT)
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    RETURN CASE day_num
        WHEN 0 THEN 'Montag'
        WHEN 1 THEN 'Dienstag'
        WHEN 2 THEN 'Mittwoch'
        WHEN 3 THEN 'Donnerstag'
        WHEN 4 THEN 'Freitag'
        WHEN 5 THEN 'Samstag'
        WHEN 6 THEN 'Sonntag'
        ELSE 'Unknown'
    END;
END //

DELIMITER ;

-- ============================================================================
-- TABLE COMMENTS
-- ============================================================================

ALTER TABLE weekly_schedules COMMENT = 'Stores weekly schedule periods';
ALTER TABLE groups COMMENT = 'Kindergarten groups (Französische, Minis, etc.)';
ALTER TABLE staff COMMENT = 'All personnel/employees';
ALTER TABLE schedule_entries COMMENT = 'Individual shift entries for each staff member per day';
