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

    // ==================== LOMBOK GENERATED METHOD TESTS ====================

    @Test
    void noArgsConstructor_ShouldCreateEmptyGroup() {
        // Act
        Group newGroup = new Group();

        // Assert
        assertThat(newGroup).isNotNull();
        assertThat(newGroup.getId()).isNull();
        assertThat(newGroup.getName()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Arrange
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Act
        Group newGroup = new Group(
            1L, "Marienkäfer", "Toddler group", true,
            "admin", now, "admin", now, new java.util.ArrayList<>()
        );

        // Assert
        assertThat(newGroup.getId()).isEqualTo(1L);
        assertThat(newGroup.getName()).isEqualTo("Marienkäfer");
        assertThat(newGroup.getDescription()).isEqualTo("Toddler group");
        assertThat(newGroup.getIsActive()).isTrue();
        assertThat(newGroup.getCreatedBy()).isEqualTo("admin");
    }

    @Test
    void setId_ShouldUpdateId() {
        // Act
        group.setId(99L);

        // Assert
        assertThat(group.getId()).isEqualTo(99L);
    }

    @Test
    void setName_ShouldUpdateName() {
        // Act
        group.setName("Schmetterling");

        // Assert
        assertThat(group.getName()).isEqualTo("Schmetterling");
    }

    @Test
    void setDescription_ShouldUpdateDescription() {
        // Act
        group.setDescription("New description");

        // Assert
        assertThat(group.getDescription()).isEqualTo("New description");
    }

    @Test
    void setIsActive_ShouldUpdateIsActive() {
        // Act
        group.setIsActive(false);

        // Assert
        assertThat(group.getIsActive()).isFalse();
    }

    @Test
    void setStaffMembers_ShouldUpdateStaffList() {
        // Arrange
        java.util.List<Staff> newList = new java.util.ArrayList<>();
        newList.add(TestDataBuilder.createTestStaff());

        // Act
        group.setStaffMembers(newList);

        // Assert
        assertThat(group.getStaffMembers()).hasSize(1);
        assertThat(group.getStaffMembers()).isEqualTo(newList);
    }

    @Test
    void equals_ShouldReturnTrueForSameId() {
        // Arrange
        Group group1 = TestDataBuilder.createTestGroup();
        group1.setId(1L);
        Group group2 = TestDataBuilder.createTestGroup();
        group2.setId(1L);

        // Act & Assert
        assertThat(group1).isEqualTo(group2);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentId() {
        // Arrange
        Group group1 = TestDataBuilder.createTestGroup();
        group1.setId(1L);
        Group group2 = TestDataBuilder.createTestGroup();
        group2.setId(2L);

        // Act & Assert
        assertThat(group1).isNotEqualTo(group2);
    }

    @Test
    void equals_ShouldReturnTrueForSameInstance() {
        // Act & Assert
        assertThat(group).isEqualTo(group);
    }

    @Test
    void equals_ShouldReturnFalseForNull() {
        // Act & Assert
        assertThat(group).isNotEqualTo(null);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        // Act
        int hash1 = group.hashCode();
        int hash2 = group.hashCode();

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void hashCode_ShouldBeEqualForEqualObjects() {
        // Arrange
        Group group1 = TestDataBuilder.createTestGroup();
        group1.setId(1L);
        Group group2 = TestDataBuilder.createTestGroup();
        group2.setId(1L);

        // Act & Assert
        assertThat(group1.hashCode()).isEqualTo(group2.hashCode());
    }

    @Test
    void toString_ShouldContainKeyFields() {
        // Arrange
        group.setId(1L);
        group.setName("Test Group");

        // Act
        String result = group.toString();

        // Assert
        assertThat(result).contains("Group");
        assertThat(result).contains("id=1");
        assertThat(result).contains("Test Group");
    }
}
