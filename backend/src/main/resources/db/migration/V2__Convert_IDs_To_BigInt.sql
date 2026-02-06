-- ============================================================================
-- MIGRATION V2: Convert ID columns from INT to BIGINT
-- ============================================================================
-- Description: Changes all primary key and foreign key columns from INT to BIGINT
--              to match Java Long type and allow for larger ID ranges
-- Date: 2026-02-06
-- ============================================================================

-- Step 1: Drop foreign key constraints
ALTER TABLE schedule_entries DROP FOREIGN KEY schedule_entries_ibfk_1;
ALTER TABLE schedule_entries DROP FOREIGN KEY schedule_entries_ibfk_2;
ALTER TABLE staff DROP FOREIGN KEY staff_ibfk_1;

-- Step 2: Convert all ID columns to BIGINT
ALTER TABLE admins MODIFY COLUMN id BIGINT AUTO_INCREMENT;
ALTER TABLE weekly_schedules MODIFY COLUMN id BIGINT AUTO_INCREMENT;
ALTER TABLE age_groups MODIFY COLUMN id BIGINT AUTO_INCREMENT;
ALTER TABLE staff MODIFY COLUMN id BIGINT AUTO_INCREMENT;
ALTER TABLE staff MODIFY COLUMN group_id BIGINT;
ALTER TABLE schedule_entries MODIFY COLUMN id BIGINT AUTO_INCREMENT;
ALTER TABLE schedule_entries MODIFY COLUMN weekly_schedule_id BIGINT NOT NULL;
ALTER TABLE schedule_entries MODIFY COLUMN staff_id BIGINT NOT NULL;

-- Step 3: Recreate foreign key constraints
ALTER TABLE staff
ADD CONSTRAINT staff_ibfk_1
FOREIGN KEY (group_id) REFERENCES age_groups(id) ON DELETE SET NULL;

ALTER TABLE schedule_entries
ADD CONSTRAINT schedule_entries_ibfk_1
FOREIGN KEY (weekly_schedule_id) REFERENCES weekly_schedules(id) ON DELETE CASCADE;

ALTER TABLE schedule_entries
ADD CONSTRAINT schedule_entries_ibfk_2
FOREIGN KEY (staff_id) REFERENCES staff(id) ON DELETE CASCADE;

-- Verify the changes
SELECT 'ID columns successfully converted to BIGINT' AS message;
