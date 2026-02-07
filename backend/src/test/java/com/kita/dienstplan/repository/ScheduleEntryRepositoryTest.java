package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.*;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ScheduleEntryRepository
 * Tests custom JPQL queries, aggregations, and database operations
 * Uses H2 in-memory database for fast, isolated tests
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
@Sql(scripts = "/test-schema.sql")
class ScheduleEntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;

    private Group group1;
    private Group group2;
    private Staff staff1;
    private Staff staff2;
    private Staff praktikant;
    private WeeklySchedule week5_2026;
    private WeeklySchedule week6_2026;

    @BeforeEach
    void setUp() {
        // Create groups
        group1 = new Group();
        group1.setName("Käfer");
        group1.setDescription("Die Käfergruppe");
        group1.setIsActive(true);
        entityManager.persist(group1);

        group2 = new Group();
        group2.setName("Bienen");
        group2.setDescription("Die Bienengruppe");
        group2.setIsActive(true);
        entityManager.persist(group2);

        // Create staff
        staff1 = new Staff();
        staff1.setFirstName("Max");
        staff1.setLastName("Mustermann");
        staff1.setFullName("Max Mustermann");
        staff1.setRole("Erzieher");
        staff1.setGroup(group1);
        staff1.setIsPraktikant(false);
        staff1.setIsActive(true);
        entityManager.persist(staff1);

        staff2 = new Staff();
        staff2.setFirstName("Lisa");
        staff2.setLastName("Schmidt");
        staff2.setFullName("Lisa Schmidt");
        staff2.setRole("Erzieher");
        staff2.setGroup(group2);
        staff2.setIsPraktikant(false);
        staff2.setIsActive(true);
        entityManager.persist(staff2);

        praktikant = new Staff();
        praktikant.setFirstName("Anna");
        praktikant.setLastName("Praktikant");
        praktikant.setFullName("Anna Praktikant");
        praktikant.setRole("Praktikant");
        praktikant.setGroup(group1);
        praktikant.setIsPraktikant(true);
        praktikant.setIsActive(true);
        entityManager.persist(praktikant);

        // Create weekly schedules
        week5_2026 = new WeeklySchedule();
        week5_2026.setWeekNumber(5);
        week5_2026.setYear(2026);
        week5_2026.setStartDate(LocalDate.of(2026, 2, 2));
        week5_2026.setEndDate(LocalDate.of(2026, 2, 8));
        entityManager.persist(week5_2026);

        week6_2026 = new WeeklySchedule();
        week6_2026.setWeekNumber(6);
        week6_2026.setYear(2026);
        week6_2026.setStartDate(LocalDate.of(2026, 2, 9));
        week6_2026.setEndDate(LocalDate.of(2026, 2, 15));
        entityManager.persist(week6_2026);

        entityManager.flush();
    }

    @Test
    void findByWeekNumberAndYear_ShouldReturnEntriesWithStaff() {
        // Arrange
        ScheduleEntry entry1 = createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
        ScheduleEntry entry2 = createEntry(week5_2026, staff2, 0, LocalTime.of(9, 0), LocalTime.of(17, 0));
        ScheduleEntry entry3 = createEntry(week6_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Different week

        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findByWeekNumberAndYear(5, 2026);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.contains(entry1));
        assertTrue(results.contains(entry2));
        assertFalse(results.contains(entry3), "Entry from different week should not be included");

        // Verify JOIN FETCH worked (no lazy loading exception)
        assertNotNull(results.get(0).getStaff().getFullName());
        assertNotNull(results.get(0).getWeeklySchedule().getWeekNumber());
    }

    @Test
    void findByStaffAndWeek_ShouldReturnOnlyStaffEntries() {
        // Arrange
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
        createEntry(week5_2026, staff1, 1, LocalTime.of(8, 0), LocalTime.of(16, 0));
        createEntry(week5_2026, staff2, 0, LocalTime.of(9, 0), LocalTime.of(17, 0)); // Different staff

        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findByStaffAndWeek(staff1.getId(), 5, 2026);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(e -> e.getStaff().getId().equals(staff1.getId())));
    }

    @Test
    void findByWorkDateOrderByStaff_FullName_ShouldOrderByName() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        createEntry(week5_2026, staff2, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Lisa (second alphabetically)
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Max (last alphabetically)
        createEntry(week5_2026, praktikant, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Anna (first alphabetically)

        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findByWorkDateOrderByStaff_FullName(testDate);

        // Assert
        assertEquals(3, results.size());
        assertEquals("Anna Praktikant", results.get(0).getStaff().getFullName());
        assertEquals("Lisa Schmidt", results.get(1).getStaff().getFullName());
        assertEquals("Max Mustermann", results.get(2).getStaff().getFullName());
    }

    @Test
    void findWhoIsWorkingAt_ShouldReturnOnlyActiveStaffAtTime() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));  // 8-16
        createEntry(week5_2026, staff2, 0, LocalTime.of(9, 0), LocalTime.of(17, 0));  // 9-17
        createEntry(week5_2026, praktikant, 0, LocalTime.of(14, 0), LocalTime.of(22, 0)); // 14-22

        // Act - Query at 10:00 (should return staff1 and staff2)
        List<ScheduleEntry> at10 = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(10, 0));

        // Assert
        assertEquals(2, at10.size());
        assertTrue(at10.stream().anyMatch(e -> e.getStaff().equals(staff1)));
        assertTrue(at10.stream().anyMatch(e -> e.getStaff().equals(staff2)));

        // Act - Query at 15:00 (should return all three)
        List<ScheduleEntry> at15 = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(15, 0));

        // Assert
        assertEquals(3, at15.size());

        // Act - Query at 18:00 (should return only praktikant)
        List<ScheduleEntry> at18 = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(18, 0));

        // Assert
        assertEquals(1, at18.size());
        assertEquals(praktikant, at18.get(0).getStaff());
    }

    @Test
    void findWhoIsWorkingAt_WithNonNormalStatus_ShouldExclude() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // normal

        ScheduleEntry krankEntry = createEntry(week5_2026, staff2, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
        krankEntry.setStatus("krank");
        entityManager.persist(krankEntry);

        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(10, 0));

        // Assert
        assertEquals(1, results.size());
        assertEquals(staff1, results.get(0).getStaff());
    }

    @Test
    void findByStatusAndDateRange_ShouldFilterCorrectly() {
        // Arrange
        LocalDate start = LocalDate.of(2026, 2, 2);
        LocalDate end = LocalDate.of(2026, 2, 5);

        ScheduleEntry krank1 = createEntry(week5_2026, staff1, 0, null, null);
        krank1.setWorkDate(LocalDate.of(2026, 2, 2));
        krank1.setStatus("krank");
        entityManager.persist(krank1);

        ScheduleEntry krank2 = createEntry(week5_2026, staff2, 1, null, null);
        krank2.setWorkDate(LocalDate.of(2026, 2, 3));
        krank2.setStatus("krank");
        entityManager.persist(krank2);

        // Normal entry (should not be included)
        createEntry(week5_2026, praktikant, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));

        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findByStatusAndDateRange("krank", start, end);

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(e -> e.getStatus().equals("krank")));
    }

    @Test
    void getDailyTotals_ShouldAggregateCorrectly() {
        // Arrange - Create entries for Monday
        ScheduleEntry entry1 = createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
        entry1.calculateWorkingHours(); // 450 minutes

        ScheduleEntry entry2 = createEntry(week5_2026, staff2, 0, LocalTime.of(9, 0), LocalTime.of(17, 0));
        entry2.calculateWorkingHours(); // 450 minutes

        ScheduleEntry entry3 = createEntry(week5_2026, praktikant, 0, LocalTime.of(8, 0), LocalTime.of(13, 0));
        entry3.calculateWorkingHours(); // 300 minutes

        entityManager.flush();

        // Act
        List<Object[]> results = scheduleEntryRepository.getDailyTotals(5, 2026);

        // Assert
        assertFalse(results.isEmpty());

        Object[] mondayTotals = results.get(0);
        assertEquals(0, mondayTotals[0]); // dayOfWeek
        assertEquals(LocalDate.of(2026, 2, 2), mondayTotals[1]); // workDate
        assertEquals(900L, mondayTotals[2]); // hours without Praktikanten (450 + 450)
        assertEquals(1200L, mondayTotals[3]); // total hours (450 + 450 + 300)
        assertEquals(2L, mondayTotals[4]); // count without Praktikanten
        assertEquals(3L, mondayTotals[5]); // total count
    }

    @Test
    void getWeeklyStaffTotals_ShouldCalculateStaffSummaries() {
        // Arrange - Create multiple entries for staff1
        ScheduleEntry mon = createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
        mon.calculateWorkingHours(); // 450 min

        ScheduleEntry tue = createEntry(week5_2026, staff1, 1, LocalTime.of(8, 0), LocalTime.of(16, 0));
        tue.calculateWorkingHours(); // 450 min

        ScheduleEntry wed = createEntry(week5_2026, staff1, 2, null, null);
        wed.setStatus("krank"); // Sick day
        wed.calculateWorkingHours(); // 0 min

        entityManager.flush();

        // Act
        List<Object[]> results = scheduleEntryRepository.getWeeklyStaffTotals(5, 2026);

        // Assert
        assertFalse(results.isEmpty());

        Object[] staff1Totals = results.stream()
            .filter(row -> row[0].equals(staff1.getId()))
            .findFirst()
            .orElseThrow();

        assertEquals(staff1.getId(), staff1Totals[0]); // staff ID
        assertEquals("Max Mustermann", staff1Totals[1]); // fullName
        assertEquals("Erzieher", staff1Totals[2]); // role
        assertEquals("Käfer", staff1Totals[3]); // group name
        assertEquals(900L, staff1Totals[4]); // total working hours
        assertEquals(60L, staff1Totals[5]); // total break minutes (30 + 30)
        assertEquals(2L, staff1Totals[6]); // normal days
        assertEquals(1L, staff1Totals[7]); // sick days
        assertEquals(0L, staff1Totals[8]); // free days
        assertEquals(0L, staff1Totals[9]); // school days
    }

    @Test
    void existsByWeeklySchedule_IdAndStaff_IdAndDayOfWeek_ShouldDetectDuplicates() {
        // Arrange
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));

        // Act & Assert
        assertTrue(scheduleEntryRepository.existsByWeeklySchedule_IdAndStaff_IdAndDayOfWeek(
            week5_2026.getId(), staff1.getId(), 0),
            "Should detect existing entry");

        assertFalse(scheduleEntryRepository.existsByWeeklySchedule_IdAndStaff_IdAndDayOfWeek(
            week5_2026.getId(), staff1.getId(), 1),
            "Should return false for non-existing day");

        assertFalse(scheduleEntryRepository.existsByWeeklySchedule_IdAndStaff_IdAndDayOfWeek(
            week5_2026.getId(), staff2.getId(), 0),
            "Should return false for different staff");
    }

    @Test
    void getDailyTotals_WithEmptyWeek_ShouldReturnEmptyList() {
        // Act - Query week with no entries
        List<Object[]> results = scheduleEntryRepository.getDailyTotals(99, 2026);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findByWeekNumberAndYear_WithNoEntries_ShouldReturnEmptyList() {
        // Act
        List<ScheduleEntry> results = scheduleEntryRepository.findByWeekNumberAndYear(99, 2026);

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void findWhoIsWorkingAt_AtBoundaryTime_ShouldIncludeEntry() {
        // Arrange - Entry from 8:00 to 16:00
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));

        // Act - Query at exact start and end times
        List<ScheduleEntry> atStart = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(8, 0));
        List<ScheduleEntry> atEnd = scheduleEntryRepository.findWhoIsWorkingAt(testDate, LocalTime.of(16, 0));

        // Assert - Boundary times should be included (>= and <=)
        assertEquals(1, atStart.size());
        assertEquals(1, atEnd.size());
    }

    @Test
    void getWeeklyStaffTotals_ShouldOrderByGroupAndName() {
        // Arrange - Create entries for multiple staff in different groups
        createEntry(week5_2026, staff2, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Group: Bienen
        createEntry(week5_2026, staff1, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Group: Käfer
        createEntry(week5_2026, praktikant, 0, LocalTime.of(8, 0), LocalTime.of(16, 0)); // Group: Käfer

        entityManager.flush();

        // Act
        List<Object[]> results = scheduleEntryRepository.getWeeklyStaffTotals(5, 2026);

        // Assert - Should be ordered by group name, then staff name
        assertEquals(3, results.size());

        // Bienen group comes before Käfer alphabetically
        assertEquals("Bienen", results.get(0)[3]);

        // Within Käfer group, Anna comes before Max alphabetically
        assertEquals("Käfer", results.get(1)[3]);
        assertEquals("Anna Praktikant", results.get(1)[1]);

        assertEquals("Käfer", results.get(2)[3]);
        assertEquals("Max Mustermann", results.get(2)[1]);
    }

    // Helper method to create and persist schedule entries
    private ScheduleEntry createEntry(WeeklySchedule schedule, Staff staff, int dayOfWeek,
                                      LocalTime startTime, LocalTime endTime) {
        ScheduleEntry entry = new ScheduleEntry();
        entry.setWeeklySchedule(schedule);
        entry.setStaff(staff);
        entry.setDayOfWeek(dayOfWeek);
        entry.setWorkDate(schedule.getStartDate().plusDays(dayOfWeek));
        entry.setStartTime(startTime);
        entry.setEndTime(endTime);
        entry.setStatus("normal");
        return entityManager.persist(entry);
    }
}
