-- ============================================================================
-- KITA CASA AZUL - Import Historical Staff Data
-- ============================================================================
-- Flyway Migration V3
-- Description: Imports 22 staff members extracted from historical PDF schedules
-- Author: System
-- Date: 2026-02-06
-- Source: PDF schedules from data/weekly-schedules (August 2025 - February 2026)
-- ============================================================================

-- ============================================================================
-- STAFF DATA: Core Team (Französische Kindergruppe)
-- ============================================================================

-- Omar Alaoui - Erzieher fr. (Educator, French group)
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Omar' as first_name,
           'Alaoui' as last_name,
           'Omar Alaoui' as full_name,
           'Erzieher' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Vollzeit' as employment_type,
           40.00 as weekly_hours,
           'omar.alaoui@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Omar Alaoui');

-- Isabel Sovic - Erzieherin pt. (Educator, part-time)
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Isabel' as first_name,
           'Sovic' as last_name,
           'Isabel Sovic' as full_name,
           'Erzieherin' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           30.00 as weekly_hours,
           'isabel.sovic@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Isabel Sovic');

-- Prudence Noubissie Deutcheu - Erzieherin fr. (Educator, French group)
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Prudence' as first_name,
           'Noubissie Deutcheu' as last_name,
           'Prudence Noubissie Deutcheu' as full_name,
           'Erzieherin' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Vollzeit' as employment_type,
           40.00 as weekly_hours,
           'prudence.noubissie@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Prudence Noubissie Deutcheu');

-- ============================================================================
-- STAFF DATA: Minis Group
-- ============================================================================

-- Bertheline Kock Nkoue Epse Tiwe - Erzieherin Mini
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Bertheline' as first_name,
           'Kock Nkoue Epse Tiwe' as last_name,
           'Bertheline Kock Nkoue Epse Tiwe' as full_name,
           'Erzieherin Mini' as role,
           (SELECT id FROM age_groups WHERE name = 'Minis' LIMIT 1) as group_id,
           'Vollzeit' as employment_type,
           40.00 as weekly_hours,
           'bertheline.kock@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Bertheline Kock Nkoue Epse Tiwe');

-- Eunice Ellen Silva - Erzieherin Mini
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Eunice Ellen' as first_name,
           'Silva' as last_name,
           'Eunice Ellen Silva' as full_name,
           'Erzieherin Mini' as role,
           (SELECT id FROM age_groups WHERE name = 'Minis' LIMIT 1) as group_id,
           'Vollzeit' as employment_type,
           40.00 as weekly_hours,
           'eunice.silva@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Eunice Ellen Silva');

-- Marion Dehnel - Sozialassisstentin Mini
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Marion' as first_name,
           'Dehnel' as last_name,
           'Marion Dehnel' as full_name,
           'Sozialassisstentin Mini' as role,
           (SELECT id FROM age_groups WHERE name = 'Minis' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           30.00 as weekly_hours,
           'marion.dehnel@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Marion Dehnel');

-- Rick Otto - Azubi (Trainee)
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Rick' as first_name,
           'Otto' as last_name,
           'Rick Otto' as full_name,
           'Azubi' as role,
           (SELECT id FROM age_groups WHERE name = 'Minis' LIMIT 1) as group_id,
           'Vollzeit' as employment_type,
           40.00 as weekly_hours,
           'rick.otto@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Rick Otto');

-- ============================================================================
-- STAFF DATA: Portugiesische Kindergruppe
-- ============================================================================

-- Elisa de Sá Zua Caldeira - Erzieherin pt.
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Elisa' as first_name,
           'de Sá Zua Caldeira' as last_name,
           'Elisa de Sá Zua Caldeira' as full_name,
           'Erzieherin' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           35.00 as weekly_hours,
           'elisa.zua@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Elisa de Sá Zua Caldeira');

-- Camilla Mannshardt Oliveira - Erzieherin pt.
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Camilla' as first_name,
           'Mannshardt Oliveira' as last_name,
           'Camilla Mannshardt Oliveira' as full_name,
           'Erzieherin' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           30.00 as weekly_hours,
           'camilla.oliveira@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Camilla Mannshardt Oliveira');

-- Alexandre Zua Caldeira - Erzieherin pt.
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Alexandre' as first_name,
           'Zua Caldeira' as last_name,
           'Alexandre Zua Caldeira' as full_name,
           'Erzieherin' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           30.00 as weekly_hours,
           'alexandre.zua@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Alexandre Zua Caldeira');

-- Letícia Viana - Assisstentin pt.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Letícia' as first_name,
           'Viana' as last_name,
           'Letícia Viana' as full_name,
           'Assisstentin' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Teilzeit' as employment_type,
           25.00 as weekly_hours,
           'leticia.viana@kitacasaazul.de' as email,
           FALSE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Letícia Viana');

-- ============================================================================
-- STAFF DATA: Praktikanten (Interns) - French Group
-- ============================================================================

-- Violetta Hristozova - Praktikantin fr.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Violetta' as first_name,
           'Hristozova' as last_name,
           'Violetta Hristozova' as full_name,
           'Praktikantin' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'violetta.hristozova@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Violetta Hristozova');

-- Hendrixa Kogningbo - Praktikantin fr.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Hendrixa' as first_name,
           'Kogningbo' as last_name,
           'Hendrixa Kogningbo' as full_name,
           'Praktikantin' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'hendrixa.kogningbo@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Hendrixa Kogningbo');

-- Fritz Sievers - Praktikant fr.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Fritz' as first_name,
           'Sievers' as last_name,
           'Fritz Sievers' as full_name,
           'Praktikant' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'fritz.sievers@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Fritz Sievers');

-- Yu Lou - Praktikant fr.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Yu' as first_name,
           'Lou' as last_name,
           'Yu Lou' as full_name,
           'Praktikant' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'yu.lou@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Yu Lou');

-- Karin Harboe - Praktikantin fr.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Karin' as first_name,
           'Harboe' as last_name,
           'Karin Harboe' as full_name,
           'Praktikantin' as role,
           (SELECT id FROM age_groups WHERE name = 'Französische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'karin.harboe@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Karin Harboe');

-- ============================================================================
-- STAFF DATA: Praktikanten (Interns) - Portuguese Group
-- ============================================================================

-- João Pedro Amaral Ferreira - Praktikant pt.-Gruppe
-- Note: There was a spelling variation "Amoral" in some PDFs - this is the correct spelling
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'João Pedro' as first_name,
           'Amaral Ferreira' as last_name,
           'João Pedro Amaral Ferreira' as full_name,
           'Praktikant' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'joao.ferreira@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'João Pedro Amaral Ferreira');

-- Iara Ferreira - Praktikant pt.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Iara' as first_name,
           'Ferreira' as last_name,
           'Iara Ferreira' as full_name,
           'Praktikant' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'iara.ferreira@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Iara Ferreira');

-- Camilla Weber - Praktikant pt.-Gruppe
INSERT INTO staff (first_name, last_name, full_name, role, group_id, employment_type, weekly_hours, email, is_praktikant, is_active, created_by)
SELECT * FROM (
    SELECT 'Camilla' as first_name,
           'Weber' as last_name,
           'Camilla Weber' as full_name,
           'Praktikant' as role,
           (SELECT id FROM age_groups WHERE name = 'Portugiesische Kindergruppe' LIMIT 1) as group_id,
           'Praktikum' as employment_type,
           30.00 as weekly_hours,
           'camilla.weber@kitacasaazul.de' as email,
           TRUE as is_praktikant,
           TRUE as is_active,
           'migration' as created_by
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM staff WHERE full_name = 'Camilla Weber');

-- ============================================================================
-- MIGRATION SUMMARY
-- ============================================================================
-- Total staff imported: 19
--
-- Breakdown by group:
-- - Französische Kindergruppe: 8 (3 core + 5 interns)
-- - Minis: 4 (3 core + 1 trainee)
-- - Portugiesische Kindergruppe: 7 (4 core + 3 interns)
--
-- Breakdown by type:
-- - Core staff (permanent): 10
-- - Interns (Praktikanten): 8
-- - Trainee (Azubi): 1
--
-- Note: Staff IDs will be auto-assigned starting from 1
-- These IDs should be used in the staff mapping file: data/staff-mapping.json
-- ============================================================================
