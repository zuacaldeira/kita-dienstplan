-- ============================================================================
-- SAMPLE DATA - Kita Casa Azul Dienstplan (MySQL)
-- Based on Week 6, 02.02.2026 - 06.02.2026
-- ============================================================================

-- Insert Groups
INSERT INTO `age_groups` (name, description) VALUES
    ('Französische Kindergruppe', 'French kindergarten group'),
    ('Minis', 'Mini children group'),
    ('Portugiesische Kindergruppe', 'Portuguese kindergarten group'),
    ('Windelmanagement', 'Diaper management for French and Portuguese groups')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Insert Weekly Schedule
INSERT INTO weekly_schedules (week_number, year, start_date, end_date, notes)
VALUES (6, 2026, '2026-02-02', '2026-02-06', 'Week 6 schedule')
ON DUPLICATE KEY UPDATE notes = VALUES(notes);

-- ========================================================================
-- FRANZÖSISCHE KINDERGRUPPE
-- ========================================================================

-- Alaoui Omar - Erzieher fr.
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Alaoui Omar', 'Omar', 'Alaoui', 'Erzieher fr.', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Französische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '07:30', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Alaoui Omar'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '08:30', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Alaoui Omar'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Alaoui Omar'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Alaoui Omar'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Alaoui Omar'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Sovic Isabel - Erzieherin pt.
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Sovic Isabel', 'Isabel', 'Sovic', 'Erzieherin pt.', g.id, 'part-time', FALSE
FROM `age_groups` g WHERE g.name = 'Französische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Sovic Isabel'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Sovic Isabel'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '09:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Sovic Isabel'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Sovic Isabel'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:15', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Sovic Isabel'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Noubissie Deutcheu Prudence - Erzieherin fr.
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Noubissie Deutcheu Prudence', 'Prudence', 'Noubissie Deutcheu', 'Erzieherin fr.', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Französische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '09:00', 'krank'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Noubissie Deutcheu Prudence'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '09:00', 'krank'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Noubissie Deutcheu Prudence'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '16:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Noubissie Deutcheu Prudence'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '08:30', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Noubissie Deutcheu Prudence'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '08:30', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Noubissie Deutcheu Prudence'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- ========================================================================
-- MINIS
-- ========================================================================

-- Kock Nkoue Epse Tiwe Bertheline
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Kock Nkoue Epse Tiwe Bertheline', 'Bertheline', 'Kock Nkoue Epse Tiwe', 'Erzieherin Mini', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Minis'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '08:30', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kock Nkoue Epse Tiwe Bertheline'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kock Nkoue Epse Tiwe Bertheline'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kock Nkoue Epse Tiwe Bertheline'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '07:30', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kock Nkoue Epse Tiwe Bertheline'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '07:30', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kock Nkoue Epse Tiwe Bertheline'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Silva Eunice Ellen
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Silva Eunice Ellen', 'Eunice Ellen', 'Silva', 'Erzieherin Mini', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Minis'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '10:00', '10:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Silva Eunice Ellen'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '08:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Silva Eunice Ellen'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '16:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Silva Eunice Ellen'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '16:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Silva Eunice Ellen'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '12:45', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Silva Eunice Ellen'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Zua Caldeira Alexandre (YOU!)
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Zua Caldeira Alexandre', 'Alexandre', 'Zua Caldeira', 'Azubi Mini', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Minis'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Zua Caldeira Alexandre'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Zua Caldeira Alexandre'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '15:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Zua Caldeira Alexandre'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '15:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Zua Caldeira Alexandre'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '16:00', 'Fachschule'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Zua Caldeira Alexandre'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

-- Dehnel Marion
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Dehnel Marion', 'Marion', 'Dehnel', 'Sozialassisstentin Mini', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Minis'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '10:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Dehnel Marion'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '10:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Dehnel Marion'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '09:30', 'Schule'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Dehnel Marion'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '10:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Dehnel Marion'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '10:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Dehnel Marion'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- ========================================================================
-- PORTUGIESISCHE KINDERGRUPPE
-- ========================================================================

-- de Sá Zua Caldeira Elisa (YOUR WIFE!)
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'de Sá Zua Caldeira Elisa', 'Elisa', 'de Sá Zua Caldeira', 'Erzieherin pt.', g.id, 'part-time', FALSE
FROM `age_groups` g WHERE g.name = 'Portugiesische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '08:00', '15:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'de Sá Zua Caldeira Elisa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '07:30', '15:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'de Sá Zua Caldeira Elisa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '07:30', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'de Sá Zua Caldeira Elisa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '08:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'de Sá Zua Caldeira Elisa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '08:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'de Sá Zua Caldeira Elisa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Viana Letícia
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Viana Letícia', 'Letícia', 'Viana', 'Assisstentin pt.-Gruppe', g.id, 'part-time', FALSE
FROM `age_groups` g WHERE g.name = 'Portugiesische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Viana Letícia'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Viana Letícia'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '08:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Viana Letícia'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Viana Letícia'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Viana Letícia'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

-- Otto Rick
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Otto Rick', 'Rick', 'Otto', 'Azubi pt.', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Portugiesische Kindergruppe'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '16:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Rick'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '16:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Rick'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Rick'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '09:00', 'Schule'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Rick'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '09:00', 'Schule'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Rick'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

-- ========================================================================
-- WINDELMANAGEMENT
-- ========================================================================

-- Otto Uwe
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Otto Uwe', 'Uwe', 'Otto', 'Windelmanagement', g.id, 'full-time', FALSE
FROM `age_groups` g WHERE g.name = 'Windelmanagement'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '17:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Uwe'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '18:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Uwe'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '08:00', '18:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Uwe'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '18:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Uwe'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '16:00', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Otto Uwe'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Kogningbo Hendrixa - Praktikantin
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Kogningbo Hendrixa', 'Hendrixa', 'Kogningbo', 'Praktikantin fr.-Gruppe', g.id, 'intern', TRUE
FROM `age_groups` g WHERE g.name = 'Windelmanagement'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kogningbo Hendrixa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kogningbo Hendrixa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kogningbo Hendrixa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kogningbo Hendrixa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '15:30', 'normal'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Kogningbo Hendrixa'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time);

-- Weber Camilla - Praktikant
INSERT INTO staff (full_name, first_name, last_name, role, group_id, employment_type, is_praktikant)
SELECT 'Weber Camilla', 'Camilla', 'Weber', 'Praktikant pt.-Gruppe', g.id, 'intern', TRUE
FROM `age_groups` g WHERE g.name = 'Windelmanagement'
ON DUPLICATE KEY UPDATE role = VALUES(role);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 0, '2026-02-02', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Weber Camilla'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 1, '2026-02-03', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Weber Camilla'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 2, '2026-02-04', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Weber Camilla'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 3, '2026-02-05', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Weber Camilla'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);

INSERT INTO schedule_entries (weekly_schedule_id, staff_id, day_of_week, work_date, start_time, end_time, status)
SELECT ws.id, s.id, 4, '2026-02-06', '09:00', '09:00', 'frei'
FROM weekly_schedules ws, staff s
WHERE ws.week_number = 6 AND ws.year = 2026 AND s.full_name = 'Weber Camilla'
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time), end_time = VALUES(end_time), status = VALUES(status);
