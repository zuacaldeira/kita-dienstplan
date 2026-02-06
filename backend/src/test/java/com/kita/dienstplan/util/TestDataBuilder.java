package com.kita.dienstplan.util;

import com.kita.dienstplan.entity.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Utility class for building test data entities
 * Provides factory methods with sensible defaults for testing
 */
public class TestDataBuilder {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Create a test admin with BCrypt-encoded password
     * Default username: testadmin, password: password123
     */
    public static Admin createTestAdmin() {
        return createTestAdmin("testadmin", "password123", "Test Admin");
    }

    /**
     * Create a test admin with custom username and password
     */
    public static Admin createTestAdmin(String username, String rawPassword, String fullName) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setFullName(fullName);
        admin.setEmail(username + "@kita-casa-azul.de");
        admin.setIsActive(true);
        return admin;
    }

    /**
     * Create an inactive test admin
     */
    public static Admin createInactiveAdmin(String username, String rawPassword) {
        Admin admin = createTestAdmin(username, rawPassword, "Inactive Admin");
        admin.setIsActive(false);
        return admin;
    }

    /**
     * Create a test staff member with defaults
     * Default: Max Mustermann, Erzieher, active, 40 hours/week
     */
    public static Staff createTestStaff() {
        return createTestStaff("Max", "Mustermann", null);
    }

    /**
     * Create a test staff member with custom name
     */
    public static Staff createTestStaff(String firstName, String lastName, Group group) {
        Staff staff = new Staff();
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setFullName(firstName + " " + lastName);
        staff.setRole("Erzieher");
        staff.setGroup(group);
        staff.setEmploymentType("full-time");
        staff.setWeeklyHours(new BigDecimal("40.00"));
        staff.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@kita.de");
        staff.setPhone("+49 123 456789");
        staff.setIsPraktikant(false);
        staff.setIsActive(true);
        staff.setHireDate(LocalDate.of(2020, 1, 1));
        return staff;
    }

    /**
     * Create a test Praktikant (intern)
     */
    public static Staff createTestPraktikant(String firstName, String lastName, Group group) {
        Staff staff = createTestStaff(firstName, lastName, group);
        staff.setRole("Praktikant");
        staff.setIsPraktikant(true);
        staff.setWeeklyHours(new BigDecimal("20.00"));
        staff.setEmploymentType("intern");
        return staff;
    }

    /**
     * Create an inactive staff member
     */
    public static Staff createInactiveStaff(String firstName, String lastName) {
        Staff staff = createTestStaff(firstName, lastName, null);
        staff.setIsActive(false);
        staff.setTerminationDate(LocalDate.now().minusMonths(1));
        return staff;
    }

    /**
     * Create a test age group with defaults
     * Default: Käfer (Beetles), active
     */
    public static Group createTestGroup() {
        return createTestGroup("Käfer", "Die Käfergruppe");
    }

    /**
     * Create a test age group with custom name
     */
    public static Group createTestGroup(String name, String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setIsActive(true);
        return group;
    }

    /**
     * Create an inactive group
     */
    public static Group createInactiveGroup(String name) {
        Group group = createTestGroup(name, "Inactive group");
        group.setIsActive(false);
        return group;
    }

    /**
     * Create a test weekly schedule for current week
     */
    public static WeeklySchedule createTestWeeklySchedule() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.GERMANY);
        int weekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int year = today.get(weekFields.weekBasedYear());

        return createTestWeeklySchedule(weekNumber, year);
    }

    /**
     * Create a test weekly schedule for specific week and year
     */
    public static WeeklySchedule createTestWeeklySchedule(int weekNumber, int year) {
        WeeklySchedule schedule = new WeeklySchedule();
        schedule.setWeekNumber(weekNumber);
        schedule.setYear(year);

        // Calculate start and end dates (Monday to Sunday)
        LocalDate startDate = LocalDate.of(year, 1, 1)
            .with(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear(), weekNumber)
            .with(WeekFields.of(Locale.GERMANY).dayOfWeek(), 1);
        LocalDate endDate = startDate.plusDays(6);

        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);
        schedule.setNotes("Test week schedule");

        return schedule;
    }

    /**
     * Create a test schedule entry with defaults
     * Default: Normal shift, 8:00-16:00, Monday
     */
    public static ScheduleEntry createTestScheduleEntry(WeeklySchedule weeklySchedule, Staff staff) {
        return createTestScheduleEntry(weeklySchedule, staff, 0, LocalTime.of(8, 0), LocalTime.of(16, 0));
    }

    /**
     * Create a test schedule entry with custom times
     */
    public static ScheduleEntry createTestScheduleEntry(
            WeeklySchedule weeklySchedule,
            Staff staff,
            int dayOfWeek,
            LocalTime startTime,
            LocalTime endTime) {

        ScheduleEntry entry = new ScheduleEntry();
        entry.setWeeklySchedule(weeklySchedule);
        entry.setStaff(staff);
        entry.setDayOfWeek(dayOfWeek);

        // Calculate work date from weekly schedule start date
        LocalDate workDate = weeklySchedule.getStartDate().plusDays(dayOfWeek);
        entry.setWorkDate(workDate);

        entry.setStartTime(startTime);
        entry.setEndTime(endTime);
        entry.setStatus("normal");
        entry.setNotes("Test entry");

        // Working hours will be calculated by @PrePersist/@PreUpdate

        return entry;
    }

    /**
     * Create a schedule entry with "frei" (day off) status
     */
    public static ScheduleEntry createFreiScheduleEntry(
            WeeklySchedule weeklySchedule,
            Staff staff,
            int dayOfWeek) {

        ScheduleEntry entry = createTestScheduleEntry(weeklySchedule, staff, dayOfWeek, null, null);
        entry.setStatus("frei");
        entry.setStartTime(null);
        entry.setEndTime(null);
        return entry;
    }

    /**
     * Create a schedule entry with "krank" (sick) status
     */
    public static ScheduleEntry createKrankScheduleEntry(
            WeeklySchedule weeklySchedule,
            Staff staff,
            int dayOfWeek) {

        ScheduleEntry entry = createTestScheduleEntry(weeklySchedule, staff, dayOfWeek, null, null);
        entry.setStatus("krank");
        entry.setStartTime(null);
        entry.setEndTime(null);
        return entry;
    }

    /**
     * Create an overnight shift entry (e.g., 22:00-06:00)
     */
    public static ScheduleEntry createOvernightScheduleEntry(
            WeeklySchedule weeklySchedule,
            Staff staff,
            int dayOfWeek) {

        return createTestScheduleEntry(
            weeklySchedule,
            staff,
            dayOfWeek,
            LocalTime.of(22, 0),
            LocalTime.of(6, 0)
        );
    }

    /**
     * Create a short shift entry (no break)
     */
    public static ScheduleEntry createShortShiftEntry(
            WeeklySchedule weeklySchedule,
            Staff staff,
            int dayOfWeek) {

        return createTestScheduleEntry(
            weeklySchedule,
            staff,
            dayOfWeek,
            LocalTime.of(8, 0),
            LocalTime.of(13, 0) // 5 hours = no break
        );
    }
}
