package com.kita.dienstplan.entity;

import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScheduleEntry entity
 * Tests working hours calculation logic, break time calculation, and lifecycle hooks
 * This is CRITICAL testing as it validates core business logic for time tracking
 */
class ScheduleEntryTest {

    private ScheduleEntry entry;
    private WeeklySchedule weeklySchedule;
    private Staff staff;

    @BeforeEach
    void setUp() {
        weeklySchedule = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        Group group = TestDataBuilder.createTestGroup();
        staff = TestDataBuilder.createTestStaff("Max", "Mustermann", group);

        entry = new ScheduleEntry();
        entry.setWeeklySchedule(weeklySchedule);
        entry.setStaff(staff);
        entry.setDayOfWeek(0); // Monday
        entry.setWorkDate(LocalDate.of(2026, 2, 3));
        entry.setStatus("normal");
    }

    @Test
    void calculateWorkingHours_StandardShift_ShouldCalculateCorrectly() {
        // Arrange - 8:00 to 16:00 = 8 hours = 480 minutes
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes(),
                "8 hours (480 min) minus 30 min break = 450 minutes (7.5 hours)");
        assertEquals(30, entry.getBreakMinutes(), "Shifts > 6 hours should have 30 minute break");
        assertEquals("7:30", entry.getFormattedWorkingHours());
    }

    @Test
    void calculateWorkingHours_ShortShift_ShouldHaveNoBreak() {
        // Arrange - 8:00 to 13:00 = 5 hours = 300 minutes
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(13, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(300, entry.getWorkingHoursMinutes(),
                "5 hours with no break = 300 minutes");
        assertEquals(0, entry.getBreakMinutes(), "Shifts <= 6 hours should have no break");
        assertEquals("5:00", entry.getFormattedWorkingHours());
    }

    @Test
    void calculateWorkingHours_OvernightShift_ShouldHandleCorrectly() {
        // Arrange - 22:00 to 06:00 = 8 hours
        entry.setStartTime(LocalTime.of(22, 0));
        entry.setEndTime(LocalTime.of(6, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes(),
                "Overnight 8 hours (480 min) minus 30 min break = 450 minutes");
        assertEquals(30, entry.getBreakMinutes());
        assertEquals("7:30", entry.getFormattedWorkingHours());
    }

    @Test
    void calculateWorkingHours_ExactlySixHours_ShouldHaveNoBreak() {
        // Arrange - 8:00 to 14:00 = exactly 6 hours
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(14, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(360, entry.getWorkingHoursMinutes(),
                "Exactly 6 hours = 360 minutes with no break");
        assertEquals(0, entry.getBreakMinutes(), "6 hours is NOT > 6, so no break");
    }

    @Test
    void calculateWorkingHours_SixHoursOneMinute_ShouldHaveBreak() {
        // Arrange - 8:00 to 14:01 = 6 hours 1 minute
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(14, 1));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(331, entry.getWorkingHoursMinutes(),
                "6 hours 1 minute (361 min) minus 30 min break = 331 minutes");
        assertEquals(30, entry.getBreakMinutes(), "Just over 6 hours triggers break");
    }

    @Test
    void calculateWorkingHours_StatusFrei_ShouldSetZero() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("frei"); // Day off

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Status 'frei' should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_StatusKrank_ShouldSetZero() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("krank"); // Sick

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Status 'krank' should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_StatusUrlaub_ShouldSetZero() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("Urlaub"); // Vacation

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Status 'Urlaub' should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_StatusFortbildung_ShouldSetZero() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("Fortbildung"); // Training

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Status 'Fortbildung' should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_NullStartTime_ShouldSetZero() {
        // Arrange
        entry.setStartTime(null);
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("normal");

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Null start time should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_NullEndTime_ShouldSetZero() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(null);
        entry.setStatus("normal");

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes(), "Null end time should result in 0 hours");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_BothTimesNull_ShouldSetZero() {
        // Arrange
        entry.setStartTime(null);
        entry.setEndTime(null);
        entry.setStatus("normal");

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(0, entry.getWorkingHoursMinutes());
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void getFormattedWorkingHours_ShouldFormatCorrectly() {
        // Arrange - Set working hours directly
        entry.setWorkingHoursMinutes(450); // 7 hours 30 minutes

        // Act
        String formatted = entry.getFormattedWorkingHours();

        // Assert
        assertEquals("7:30", formatted);
    }

    @Test
    void getFormattedWorkingHours_WithSingleDigitMinutes_ShouldPadZero() {
        // Arrange
        entry.setWorkingHoursMinutes(365); // 6 hours 5 minutes

        // Act
        String formatted = entry.getFormattedWorkingHours();

        // Assert
        assertEquals("6:05", formatted, "Minutes should be zero-padded");
    }

    @Test
    void getFormattedWorkingHours_WithZeroHours_ShouldReturnZero() {
        // Arrange
        entry.setWorkingHoursMinutes(0);

        // Act
        String formatted = entry.getFormattedWorkingHours();

        // Assert
        assertEquals("0:00", formatted);
    }

    @Test
    void getFormattedWorkingHours_WithLongShift_ShouldFormatCorrectly() {
        // Arrange - 10 hours = 600 minutes
        entry.setWorkingHoursMinutes(600);

        // Act
        String formatted = entry.getFormattedWorkingHours();

        // Assert
        assertEquals("10:00", formatted);
    }

    @Test
    void getFormattedBreakTime_ShouldFormatCorrectly() {
        // Arrange
        entry.setBreakMinutes(30);

        // Act
        String formatted = entry.getFormattedBreakTime();

        // Assert
        assertEquals("0:30", formatted);
    }

    @Test
    void prePersist_ShouldTriggerCalculation() {
        // Arrange
        entry.setStartTime(LocalTime.of(9, 0));
        entry.setEndTime(LocalTime.of(17, 0));
        entry.setStatus("normal");

        // Manually set incorrect values
        entry.setWorkingHoursMinutes(999);
        entry.setBreakMinutes(999);

        // Act - Simulate @PrePersist
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes(),
                "@PrePersist should recalculate even if values already set");
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void preUpdate_ShouldTriggerCalculation() {
        // Arrange - Initial calculation
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(14, 0));
        entry.setStatus("normal");
        entry.calculateWorkingHours();

        int initialHours = entry.getWorkingHoursMinutes();
        assertEquals(360, initialHours);

        // Act - Change times and recalculate (simulating @PreUpdate)
        entry.setEndTime(LocalTime.of(16, 0));
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes(),
                "@PreUpdate should recalculate when times change");
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_EarlyMorningShift_ShouldCalculateCorrectly() {
        // Arrange - 6:00 to 14:00 = 8 hours
        entry.setStartTime(LocalTime.of(6, 0));
        entry.setEndTime(LocalTime.of(14, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes());
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_LateShift_ShouldCalculateCorrectly() {
        // Arrange - 14:00 to 22:00 = 8 hours
        entry.setStartTime(LocalTime.of(14, 0));
        entry.setEndTime(LocalTime.of(22, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(450, entry.getWorkingHoursMinutes());
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_LongOvernightShift_ShouldCalculateCorrectly() {
        // Arrange - 20:00 to 08:00 = 12 hours
        entry.setStartTime(LocalTime.of(20, 0));
        entry.setEndTime(LocalTime.of(8, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(690, entry.getWorkingHoursMinutes(),
                "12 hours (720 min) minus 30 min break = 690 minutes");
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_PartTimeShift_ShouldCalculateCorrectly() {
        // Arrange - 9:00 to 13:00 = 4 hours
        entry.setStartTime(LocalTime.of(9, 0));
        entry.setEndTime(LocalTime.of(13, 0));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(240, entry.getWorkingHoursMinutes(), "4 hours = 240 minutes, no break");
        assertEquals(0, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_StatusCaseInsensitive_ShouldWork() {
        // Test that status check is case-insensitive

        // Test 1: "NORMAL" uppercase
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("NORMAL");
        entry.calculateWorkingHours();
        assertEquals(450, entry.getWorkingHoursMinutes());

        // Test 2: "Normal" mixed case
        entry.setStatus("Normal");
        entry.calculateWorkingHours();
        assertEquals(450, entry.getWorkingHoursMinutes());

        // Test 3: "KRANK" uppercase should be 0
        entry.setStatus("KRANK");
        entry.calculateWorkingHours();
        assertEquals(0, entry.getWorkingHoursMinutes());
    }

    @Test
    void calculateWorkingHours_WithOddMinutes_ShouldCalculateCorrectly() {
        // Arrange - 8:15 to 16:47 = 8 hours 32 minutes
        entry.setStartTime(LocalTime.of(8, 15));
        entry.setEndTime(LocalTime.of(16, 47));

        // Act
        entry.calculateWorkingHours();

        // Assert
        assertEquals(482, entry.getWorkingHoursMinutes(),
                "8h 32min (512 min) minus 30 min break = 482 minutes");
        assertEquals(30, entry.getBreakMinutes());
    }

    @Test
    void calculateWorkingHours_MultipleCalls_ShouldBeIdempotent() {
        // Arrange
        entry.setStartTime(LocalTime.of(8, 0));
        entry.setEndTime(LocalTime.of(16, 0));
        entry.setStatus("normal");

        // Act - Call multiple times
        entry.calculateWorkingHours();
        int firstResult = entry.getWorkingHoursMinutes();

        entry.calculateWorkingHours();
        int secondResult = entry.getWorkingHoursMinutes();

        entry.calculateWorkingHours();
        int thirdResult = entry.getWorkingHoursMinutes();

        // Assert - All results should be the same
        assertEquals(450, firstResult);
        assertEquals(firstResult, secondResult);
        assertEquals(secondResult, thirdResult);
    }
}
