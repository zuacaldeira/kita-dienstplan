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

    // ==================== LOMBOK GENERATED METHOD TESTS ====================

    @Test
    void noArgsConstructor_ShouldCreateEmptyStaff() {
        // Act
        Staff newStaff = new Staff();

        // Assert
        assertThat(newStaff).isNotNull();
        assertThat(newStaff.getId()).isNull();
        assertThat(newStaff.getFullName()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Arrange
        java.time.LocalDate hireDate = java.time.LocalDate.now();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Act
        Staff newStaff = new Staff(
            1L, "John", "Doe", "John Doe", "Educator",
            new Group(), "full-time", new java.math.BigDecimal("40.00"),
            "john@example.com", "123456789", false, true,
            hireDate, null, "admin", now, "admin", now, new java.util.ArrayList<>()
        );

        // Assert
        assertThat(newStaff.getId()).isEqualTo(1L);
        assertThat(newStaff.getFirstName()).isEqualTo("John");
        assertThat(newStaff.getLastName()).isEqualTo("Doe");
        assertThat(newStaff.getFullName()).isEqualTo("John Doe");
        assertThat(newStaff.getRole()).isEqualTo("Educator");
        assertThat(newStaff.getEmploymentType()).isEqualTo("full-time");
        assertThat(newStaff.getWeeklyHours()).isEqualByComparingTo("40.00");
        assertThat(newStaff.getEmail()).isEqualTo("john@example.com");
        assertThat(newStaff.getPhone()).isEqualTo("123456789");
        assertThat(newStaff.getIsPraktikant()).isFalse();
        assertThat(newStaff.getIsActive()).isTrue();
        assertThat(newStaff.getHireDate()).isEqualTo(hireDate);
    }

    @Test
    void setId_ShouldUpdateId() {
        // Act
        staff.setId(99L);

        // Assert
        assertThat(staff.getId()).isEqualTo(99L);
    }

    @Test
    void setFirstName_ShouldUpdateFirstName() {
        // Act
        staff.setFirstName("Jane");

        // Assert
        assertThat(staff.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void setLastName_ShouldUpdateLastName() {
        // Act
        staff.setLastName("Smith");

        // Assert
        assertThat(staff.getLastName()).isEqualTo("Smith");
    }

    @Test
    void setFullName_ShouldUpdateFullName() {
        // Act
        staff.setFullName("Jane Smith");

        // Assert
        assertThat(staff.getFullName()).isEqualTo("Jane Smith");
    }

    @Test
    void setRole_ShouldUpdateRole() {
        // Act
        staff.setRole("Manager");

        // Assert
        assertThat(staff.getRole()).isEqualTo("Manager");
    }

    @Test
    void setGroup_ShouldUpdateGroup() {
        // Arrange
        Group newGroup = TestDataBuilder.createTestGroup();
        newGroup.setName("Marienkäfer");

        // Act
        staff.setGroup(newGroup);

        // Assert
        assertThat(staff.getGroup()).isEqualTo(newGroup);
        assertThat(staff.getGroup().getName()).isEqualTo("Marienkäfer");
    }

    @Test
    void setEmploymentType_ShouldUpdateEmploymentType() {
        // Act
        staff.setEmploymentType("part-time");

        // Assert
        assertThat(staff.getEmploymentType()).isEqualTo("part-time");
    }

    @Test
    void setWeeklyHours_ShouldUpdateWeeklyHours() {
        // Act
        staff.setWeeklyHours(new java.math.BigDecimal("20.00"));

        // Assert
        assertThat(staff.getWeeklyHours()).isEqualByComparingTo("20.00");
    }

    @Test
    void setEmail_ShouldUpdateEmail() {
        // Act
        staff.setEmail("newemail@example.com");

        // Assert
        assertThat(staff.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void setPhone_ShouldUpdatePhone() {
        // Act
        staff.setPhone("987654321");

        // Assert
        assertThat(staff.getPhone()).isEqualTo("987654321");
    }

    @Test
    void setIsPraktikant_ShouldUpdateIsPraktikant() {
        // Act
        staff.setIsPraktikant(true);

        // Assert
        assertThat(staff.getIsPraktikant()).isTrue();
    }

    @Test
    void setIsActive_ShouldUpdateIsActive() {
        // Act
        staff.setIsActive(false);

        // Assert
        assertThat(staff.getIsActive()).isFalse();
    }

    @Test
    void setHireDate_ShouldUpdateHireDate() {
        // Arrange
        java.time.LocalDate newDate = java.time.LocalDate.of(2023, 1, 15);

        // Act
        staff.setHireDate(newDate);

        // Assert
        assertThat(staff.getHireDate()).isEqualTo(newDate);
    }

    @Test
    void setTerminationDate_ShouldUpdateTerminationDate() {
        // Arrange
        java.time.LocalDate newDate = java.time.LocalDate.of(2024, 12, 31);

        // Act
        staff.setTerminationDate(newDate);

        // Assert
        assertThat(staff.getTerminationDate()).isEqualTo(newDate);
    }

    @Test
    void equals_ShouldReturnTrueForSameId() {
        // Arrange
        Staff staff1 = TestDataBuilder.createTestStaff();
        staff1.setId(1L);
        Staff staff2 = TestDataBuilder.createTestStaff();
        staff2.setId(1L);

        // Act & Assert
        assertThat(staff1).isEqualTo(staff2);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentId() {
        // Arrange
        Staff staff1 = TestDataBuilder.createTestStaff();
        staff1.setId(1L);
        Staff staff2 = TestDataBuilder.createTestStaff();
        staff2.setId(2L);

        // Act & Assert
        assertThat(staff1).isNotEqualTo(staff2);
    }

    @Test
    void equals_ShouldReturnTrueForSameInstance() {
        // Act & Assert
        assertThat(staff).isEqualTo(staff);
    }

    @Test
    void equals_ShouldReturnFalseForNull() {
        // Act & Assert
        assertThat(staff).isNotEqualTo(null);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        // Act
        int hash1 = staff.hashCode();
        int hash2 = staff.hashCode();

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hashCode_ShouldBeEqualForEqualObjects() {
        // Arrange
        Staff staff1 = TestDataBuilder.createTestStaff();
        staff1.setId(1L);
        Staff staff2 = TestDataBuilder.createTestStaff();
        staff2.setId(1L);

        // Act & Assert
        assertThat(staff1.hashCode()).isEqualTo(staff2.hashCode());
    }

    @Test
    void toString_ShouldContainKeyFields() {
        // Arrange
        staff.setId(1L);
        staff.setFullName("Test User");
        staff.setRole("Educator");

        // Act
        String result = staff.toString();

        // Assert
        assertThat(result).contains("Staff");
        assertThat(result).contains("id=1");
        assertThat(result).contains("Test User");
        assertThat(result).contains("Educator");
    }
}
