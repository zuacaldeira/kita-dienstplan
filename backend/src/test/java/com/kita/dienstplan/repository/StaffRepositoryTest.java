package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.ScheduleEntry;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.util.TestDataBuilder;
import com.kita.dienstplan.util.TestJpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for StaffRepository custom query methods
 * Uses @DataJpaTest for real database interactions with H2 in-memory DB
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
@Sql(scripts = "/test-schema.sql")
class StaffRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StaffRepository staffRepository;

    private Group groupKaefer;
    private Group groupMarienkaefer;
    private Staff staff1;
    private Staff staff2;
    private Staff staff3;

    @BeforeEach
    void setUp() {
        // Create test groups
        groupKaefer = TestDataBuilder.createTestGroup("Käfer", "Käfer group");
        groupMarienkaefer = TestDataBuilder.createTestGroup("Marienkäfer", "Marienkäfer group");
        entityManager.persistAndFlush(groupKaefer);
        entityManager.persistAndFlush(groupMarienkaefer);

        // Create test staff
        staff1 = TestDataBuilder.createTestStaff("Max", "Mustermann", groupKaefer);
        staff2 = TestDataBuilder.createTestStaff("Anna", "Schmidt", groupKaefer);
        staff3 = TestDataBuilder.createTestStaff("Peter", "Weber", groupMarienkaefer);

        entityManager.persistAndFlush(staff1);
        entityManager.persistAndFlush(staff2);
        entityManager.persistAndFlush(staff3);
        entityManager.clear();
    }

    @Test
    void findByFullName_ExistingStaff_ShouldReturnStaff() {
        // Act
        Optional<Staff> result = staffRepository.findByFullName("Max Mustermann");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("Max Mustermann");
        assertThat(result.get().getFirstName()).isEqualTo("Max");
        assertThat(result.get().getLastName()).isEqualTo("Mustermann");
    }

    @Test
    void findByFullName_NonExistingStaff_ShouldReturnEmpty() {
        // Act
        Optional<Staff> result = staffRepository.findByFullName("John Doe");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByFullName_NullName_ShouldReturnEmpty() {
        // Act
        Optional<Staff> result = staffRepository.findByFullName(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByIsActiveTrueOrderByFullName_ShouldReturnAllActiveStaffSorted() {
        // Act
        List<Staff> result = staffRepository.findByIsActiveTrueOrderByFullName();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactly("Anna Schmidt", "Max Mustermann", "Peter Weber"); // Alphabetical order
    }

    @Test
    void findByIsActiveTrueOrderByFullName_WithInactiveStaff_ShouldExcludeInactive() {
        // Arrange - Create inactive staff
        Staff inactiveStaff = TestDataBuilder.createInactiveStaff("Inactive", "User");
        entityManager.persistAndFlush(inactiveStaff);

        // Act
        List<Staff> result = staffRepository.findByIsActiveTrueOrderByFullName();

        // Assert
        assertThat(result).hasSize(3); // Should not include inactive staff
        assertThat(result).extracting(Staff::getFullName)
            .doesNotContain("Inactive User");
    }

    @Test
    void findByGroupIdAndActive_ExistingGroup_ShouldReturnStaffInGroup() {
        // Act
        List<Staff> result = staffRepository.findByGroupIdAndActive(groupKaefer.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactlyInAnyOrder("Max Mustermann", "Anna Schmidt");
    }

    @Test
    void findByGroupIdAndActive_NonExistingGroup_ShouldReturnEmptyList() {
        // Act
        List<Staff> result = staffRepository.findByGroupIdAndActive(999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByGroupIdAndActive_WithInactiveStaff_ShouldExcludeInactive() {
        // Arrange - Create inactive staff in group
        Staff inactiveStaff = TestDataBuilder.createInactiveStaff("Inactive", "Kaefer");
        inactiveStaff.setGroup(groupKaefer);
        entityManager.persistAndFlush(inactiveStaff);

        // Act
        List<Staff> result = staffRepository.findByGroupIdAndActive(groupKaefer.getId());

        // Assert
        assertThat(result).hasSize(2); // Should not include inactive
        assertThat(result).extracting(Staff::getFullName)
            .doesNotContain("Inactive Kaefer");
    }

    @Test
    void findByGroupNameAndActive_ExistingGroupName_ShouldReturnStaffInGroup() {
        // Act
        List<Staff> result = staffRepository.findByGroupNameAndActive("Käfer");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactlyInAnyOrder("Max Mustermann", "Anna Schmidt");
    }

    @Test
    void findByGroupNameAndActive_NonExistingGroupName_ShouldReturnEmptyList() {
        // Act
        List<Staff> result = staffRepository.findByGroupNameAndActive("NonExistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByIsPraktikantTrueAndIsActiveTrueOrderByFullName_ShouldReturnOnlyPraktikanten() {
        // Arrange - Create Praktikanten
        Staff praktikant1 = TestDataBuilder.createTestPraktikant("Julia", "Becker", groupKaefer);
        Staff praktikant2 = TestDataBuilder.createTestPraktikant("Tom", "Müller", groupMarienkaefer);
        entityManager.persistAndFlush(praktikant1);
        entityManager.persistAndFlush(praktikant2);

        // Act
        List<Staff> result = staffRepository.findByIsPraktikantTrueAndIsActiveTrueOrderByFullName();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactly("Julia Becker", "Tom Müller"); // Sorted alphabetically
        assertThat(result).allMatch(Staff::getIsPraktikant);
    }

    @Test
    void findByIsPraktikantTrueAndIsActiveTrueOrderByFullName_WithInactivePraktikant_ShouldExclude() {
        // Arrange
        Staff aktivPraktikant = TestDataBuilder.createTestPraktikant("Active", "Praktikant", groupKaefer);
        Staff inaktivPraktikant = TestDataBuilder.createTestPraktikant("Inactive", "Praktikant", groupKaefer);
        inaktivPraktikant.setIsActive(false);
        entityManager.persistAndFlush(aktivPraktikant);
        entityManager.persistAndFlush(inaktivPraktikant);

        // Act
        List<Staff> result = staffRepository.findByIsPraktikantTrueAndIsActiveTrueOrderByFullName();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Active Praktikant");
    }

    @Test
    void findByRoleAndIsActiveTrueOrderByFullName_ExistingRole_ShouldReturnStaffWithRole() {
        // Act
        List<Staff> result = staffRepository.findByRoleAndIsActiveTrueOrderByFullName("Erzieher");

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).allMatch(staff -> staff.getRole().equals("Erzieher"));
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactly("Anna Schmidt", "Max Mustermann", "Peter Weber"); // Sorted
    }

    @Test
    void findByRoleAndIsActiveTrueOrderByFullName_NonExistingRole_ShouldReturnEmptyList() {
        // Act
        List<Staff> result = staffRepository.findByRoleAndIsActiveTrueOrderByFullName("Manager");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByFullName_ExistingName_ShouldReturnTrue() {
        // Act
        boolean exists = staffRepository.existsByFullName("Max Mustermann");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByFullName_NonExistingName_ShouldReturnFalse() {
        // Act
        boolean exists = staffRepository.existsByFullName("John Doe");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void findStaffWithScheduleForWeek_WithScheduleEntries_ShouldReturnStaffWithSchedules() {
        // Arrange - Create weekly schedule and entries
        WeeklySchedule weeklySchedule = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        entityManager.persistAndFlush(weeklySchedule);

        ScheduleEntry entry1 = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff1);
        ScheduleEntry entry2 = TestDataBuilder.createTestScheduleEntry(weeklySchedule, staff2);
        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);
        entityManager.clear();

        // Act
        List<Staff> result = staffRepository.findStaffWithScheduleForWeek(5, 2026);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(Staff::getFullName)
            .containsExactlyInAnyOrder("Max Mustermann", "Anna Schmidt");
    }

    @Test
    void findStaffWithScheduleForWeek_NoScheduleEntries_ShouldReturnEmptyList() {
        // Act
        List<Staff> result = staffRepository.findStaffWithScheduleForWeek(99, 2099);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findStaffWithScheduleForWeek_DifferentWeek_ShouldNotReturnStaff() {
        // Arrange
        WeeklySchedule weeklySchedule1 = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        WeeklySchedule weeklySchedule2 = TestDataBuilder.createTestWeeklySchedule(6, 2026);
        entityManager.persistAndFlush(weeklySchedule1);
        entityManager.persistAndFlush(weeklySchedule2);

        ScheduleEntry entry1 = TestDataBuilder.createTestScheduleEntry(weeklySchedule1, staff1);
        ScheduleEntry entry2 = TestDataBuilder.createTestScheduleEntry(weeklySchedule2, staff2);
        entityManager.persistAndFlush(entry1);
        entityManager.persistAndFlush(entry2);

        // Act - Query for week 5
        List<Staff> result = staffRepository.findStaffWithScheduleForWeek(5, 2026);

        // Assert - Should only return staff1
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullName()).isEqualTo("Max Mustermann");
    }
}
