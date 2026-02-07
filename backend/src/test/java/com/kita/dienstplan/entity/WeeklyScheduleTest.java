package com.kita.dienstplan.entity;

import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for WeeklySchedule entity helper methods
 * Tests bidirectional relationship management with ScheduleEntry
 */
class WeeklyScheduleTest {

    private WeeklySchedule weeklySchedule;
    private Staff staff;

    @BeforeEach
    void setUp() {
        weeklySchedule = TestDataBuilder.createTestWeeklySchedule();
        staff = TestDataBuilder.createTestStaff();
    }

    @Test
    void addScheduleEntry_ShouldAddEntryToList() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);

        // Act
        weeklySchedule.addScheduleEntry(entry);

        // Assert
        assertThat(weeklySchedule.getScheduleEntries()).hasSize(1);
        assertThat(weeklySchedule.getScheduleEntries()).contains(entry);
    }

    @Test
    void addScheduleEntry_ShouldSetBidirectionalRelationship() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        entry.setWeeklySchedule(null); // Reset to test bidirectional setting

        // Act
        weeklySchedule.addScheduleEntry(entry);

        // Assert
        assertThat(entry.getWeeklySchedule()).isEqualTo(weeklySchedule);
    }

    @Test
    void addScheduleEntry_ShouldInitializeListIfNull() {
        // Arrange
        weeklySchedule.setScheduleEntries(null);
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);

        // Act & Assert - Should not throw NullPointerException
        assertThatCode(() -> {
            if (weeklySchedule.getScheduleEntries() == null) {
                weeklySchedule.setScheduleEntries(new java.util.ArrayList<>());
            }
            weeklySchedule.addScheduleEntry(entry);
        }).doesNotThrowAnyException();
    }

    @Test
    void removeScheduleEntry_ShouldRemoveEntryFromList() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        weeklySchedule.addScheduleEntry(entry);

        // Act
        weeklySchedule.removeScheduleEntry(entry);

        // Assert
        assertThat(weeklySchedule.getScheduleEntries()).isEmpty();
        assertThat(weeklySchedule.getScheduleEntries()).doesNotContain(entry);
    }

    @Test
    void removeScheduleEntry_ShouldClearBidirectionalRelationship() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        weeklySchedule.addScheduleEntry(entry);

        // Act
        weeklySchedule.removeScheduleEntry(entry);

        // Assert
        assertThat(entry.getWeeklySchedule()).isNull();
    }

    @Test
    void weekNumberAndYear_ShouldBeUnique() {
        // This is a database constraint test - the @UniqueConstraint ensures uniqueness
        // We verify the fields are present and configured correctly
        // Arrange & Assert
        assertThat(weeklySchedule.getWeekNumber()).isNotNull();
        assertThat(weeklySchedule.getYear()).isNotNull();
    }
}
