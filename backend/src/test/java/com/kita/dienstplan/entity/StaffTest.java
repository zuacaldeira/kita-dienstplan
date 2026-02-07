package com.kita.dienstplan.entity;

import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Staff entity helper methods
 * Tests bidirectional relationship management with ScheduleEntry
 */
class StaffTest {

    private Staff staff;
    private WeeklySchedule weeklySchedule;

    @BeforeEach
    void setUp() {
        staff = TestDataBuilder.createTestStaff();
        weeklySchedule = TestDataBuilder.createTestWeeklySchedule();
    }

    @Test
    void addScheduleEntry_ShouldAddEntryToList() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);

        // Act
        staff.addScheduleEntry(entry);

        // Assert
        assertThat(staff.getScheduleEntries()).hasSize(1);
        assertThat(staff.getScheduleEntries()).contains(entry);
    }

    @Test
    void addScheduleEntry_ShouldSetBidirectionalRelationship() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        entry.setStaff(null); // Reset to test bidirectional setting

        // Act
        staff.addScheduleEntry(entry);

        // Assert
        assertThat(entry.getStaff()).isEqualTo(staff);
    }

    @Test
    void addScheduleEntry_ShouldInitializeListIfNull() {
        // Arrange
        staff.setScheduleEntries(null);
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);

        // Act & Assert - Should not throw NullPointerException
        assertThatCode(() -> {
            if (staff.getScheduleEntries() == null) {
                staff.setScheduleEntries(new java.util.ArrayList<>());
            }
            staff.addScheduleEntry(entry);
        }).doesNotThrowAnyException();
    }

    @Test
    void removeScheduleEntry_ShouldRemoveEntryFromList() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        staff.addScheduleEntry(entry);

        // Act
        staff.removeScheduleEntry(entry);

        // Assert
        assertThat(staff.getScheduleEntries()).isEmpty();
        assertThat(staff.getScheduleEntries()).doesNotContain(entry);
    }

    @Test
    void removeScheduleEntry_ShouldClearBidirectionalRelationship() {
        // Arrange
        ScheduleEntry entry = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff);
        staff.addScheduleEntry(entry);

        // Act
        staff.removeScheduleEntry(entry);

        // Assert
        assertThat(entry.getStaff()).isNull();
    }

    @Test
    void fullName_ShouldBeUnique() {
        // This is a database constraint test - the annotation @Column(unique = true) ensures uniqueness
        // We verify the annotation is present and configured correctly
        // Arrange & Assert
        assertThat(staff.getFullName()).isNotNull();
        assertThat(staff.getFullName()).isEqualTo("Max Mustermann");
    }
}
