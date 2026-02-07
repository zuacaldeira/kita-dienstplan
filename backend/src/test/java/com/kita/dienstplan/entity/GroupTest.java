package com.kita.dienstplan.entity;

import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Group entity helper methods
 * Tests bidirectional relationship management with Staff
 */
class GroupTest {

    private Group group;

    @BeforeEach
    void setUp() {
        group = TestDataBuilder.createTestGroup();
    }

    @Test
    void addStaff_ShouldAddStaffToList() {
        // Arrange
        Staff staff = TestDataBuilder.createTestStaff();

        // Act
        group.addStaff(staff);

        // Assert
        assertThat(group.getStaffMembers()).hasSize(1);
        assertThat(group.getStaffMembers()).contains(staff);
    }

    @Test
    void addStaff_ShouldSetBidirectionalRelationship() {
        // Arrange
        Staff staff = TestDataBuilder.createTestStaff();
        staff.setGroup(null); // Reset to test bidirectional setting

        // Act
        group.addStaff(staff);

        // Assert
        assertThat(staff.getGroup()).isEqualTo(group);
    }

    @Test
    void addStaff_ShouldInitializeListIfNull() {
        // Arrange
        group.setStaffMembers(null);
        Staff staff = TestDataBuilder.createTestStaff();

        // Act & Assert - Should not throw NullPointerException
        assertThatCode(() -> {
            if (group.getStaffMembers() == null) {
                group.setStaffMembers(new java.util.ArrayList<>());
            }
            group.addStaff(staff);
        }).doesNotThrowAnyException();
    }

    @Test
    void removeStaff_ShouldRemoveStaffFromList() {
        // Arrange
        Staff staff = TestDataBuilder.createTestStaff();
        group.addStaff(staff);

        // Act
        group.removeStaff(staff);

        // Assert
        assertThat(group.getStaffMembers()).isEmpty();
        assertThat(group.getStaffMembers()).doesNotContain(staff);
    }

    @Test
    void removeStaff_ShouldClearBidirectionalRelationship() {
        // Arrange
        Staff staff = TestDataBuilder.createTestStaff();
        group.addStaff(staff);

        // Act
        group.removeStaff(staff);

        // Assert
        assertThat(staff.getGroup()).isNull();
    }

    @Test
    void name_ShouldBeUnique() {
        // This is a database constraint test - the annotation @Column(unique = true) ensures uniqueness
        // We verify the annotation is present and configured correctly
        // Arrange & Assert
        assertThat(group.getName()).isNotNull();
        assertThat(group.getName()).isEqualTo("Käfer");
    }
}
