package com.kita.dienstplan.service;

import com.kita.dienstplan.dto.DailyTotalDTO;
import com.kita.dienstplan.dto.ScheduleEntryDTO;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.ScheduleEntry;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.ScheduleEntryRepository;
import com.kita.dienstplan.repository.StaffRepository;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ScheduleService
 * Tests DTO transformations, repository interactions, and business logic
 */
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleEntryRepository scheduleEntryRepository;

    @Mock
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private WeeklySchedule testWeeklySchedule;
    private Group testGroup;
    private Staff testStaff;
    private ScheduleEntry testEntry;

    @BeforeEach
    void setUp() {
        testGroup = TestDataBuilder.createTestGroup();
        testGroup.setId(1L);

        testStaff = TestDataBuilder.createTestStaff("Max", "Mustermann", testGroup);
        testStaff.setId(1L);

        testWeeklySchedule = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        testWeeklySchedule.setId(1L);

        testEntry = TestDataBuilder.createTestScheduleEntry(testWeeklySchedule, testStaff);
        testEntry.setId(1L);
        testEntry.calculateWorkingHours();
    }

    @Test
    void getScheduleForWeek_ShouldConvertToDTO() {
        // Arrange
        when(scheduleEntryRepository.findByWeekNumberAndYear(5, 2026))
                .thenReturn(Arrays.asList(testEntry));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForWeek(5, 2026);

        // Assert
        assertEquals(1, results.size());
        ScheduleEntryDTO dto = results.get(0);
        assertEquals(testEntry.getId(), dto.getId());
        assertEquals(testStaff.getFullName(), dto.getStaffName());
        assertEquals(testStaff.getRole(), dto.getStaffRole());
        assertEquals(testGroup.getName(), dto.getGroupName());
        assertNotNull(dto.getWorkingHoursFormatted());

        verify(scheduleEntryRepository, times(1)).findByWeekNumberAndYear(5, 2026);
    }

    @Test
    void getScheduleForWeek_WithEmptyResults_ShouldReturnEmptyList() {
        // Arrange
        when(scheduleEntryRepository.findByWeekNumberAndYear(99, 2026))
                .thenReturn(Collections.emptyList());

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForWeek(99, 2026);

        // Assert
        assertTrue(results.isEmpty());
        verify(scheduleEntryRepository, times(1)).findByWeekNumberAndYear(99, 2026);
    }

    @Test
    void getScheduleForStaffInWeek_ShouldFilterByStaff() {
        // Arrange
        when(scheduleEntryRepository.findByStaffAndWeek(1L, 5, 2026))
                .thenReturn(Arrays.asList(testEntry));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForStaffInWeek(1L, 5, 2026);

        // Assert
        assertEquals(1, results.size());
        assertEquals(testStaff.getId(), results.get(0).getStaffId());
        verify(scheduleEntryRepository, times(1)).findByStaffAndWeek(1L, 5, 2026);
    }

    @Test
    void getScheduleForDate_ShouldReturnEntriesForDate() {
        // Arrange
        LocalDate testDate = testEntry.getWorkDate(); // Use the entry's actual work date
        when(scheduleEntryRepository.findByWorkDateOrderByStaff_FullName(testDate))
                .thenReturn(Arrays.asList(testEntry));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForDate(testDate);

        // Assert
        assertEquals(1, results.size());
        assertEquals(testDate, results.get(0).getWorkDate());
        verify(scheduleEntryRepository, times(1)).findByWorkDateOrderByStaff_FullName(testDate);
    }

    @Test
    void getWhoIsWorkingAt_ShouldReturnActiveStaff() {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        LocalTime testTime = LocalTime.of(10, 0);
        when(scheduleEntryRepository.findWhoIsWorkingAt(testDate, testTime))
                .thenReturn(Arrays.asList(testEntry));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getWhoIsWorkingAt(testDate, testTime);

        // Assert
        assertEquals(1, results.size());
        verify(scheduleEntryRepository, times(1)).findWhoIsWorkingAt(testDate, testTime);
    }

    @Test
    void getDailyTotals_ShouldTransformObjectArrayToDTO() {
        // Arrange - Mock repository returning Object[][]
        Object[] row1 = new Object[]{
            0,                           // dayOfWeek (Monday)
            LocalDate.of(2026, 2, 2),   // workDate
            900L,                        // totalMinutesWithoutPraktikanten
            1200L,                       // totalMinutesWithPraktikanten
            2L,                          // staffCountWithoutPraktikanten
            3L                           // totalStaffCount
        };

        when(scheduleEntryRepository.getDailyTotals(5, 2026))
                .thenReturn(Collections.singletonList(row1));

        // Act
        List<DailyTotalDTO> results = scheduleService.getDailyTotals(5, 2026);

        // Assert
        assertEquals(1, results.size());

        DailyTotalDTO dto = results.get(0);
        assertEquals(0, dto.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 2), dto.getWorkDate());
        assertEquals(900, dto.getTotalMinutesWithoutPraktikanten());
        assertEquals(1200, dto.getTotalMinutesWithPraktikanten());
        assertEquals(2L, dto.getStaffCountWithoutPraktikanten());
        assertEquals(3L, dto.getTotalStaffCount());

        // Verify formatting methods were called
        assertEquals("Montag", dto.getDayName());
        assertEquals("15:00", dto.getHoursWithoutPraktikanten());
        assertEquals("20:00", dto.getHoursWithPraktikanten());

        verify(scheduleEntryRepository, times(1)).getDailyTotals(5, 2026);
    }

    @Test
    void getDailyTotals_WithMultipleDays_ShouldTransformAll() {
        // Arrange
        Object[] monday = new Object[]{0, LocalDate.of(2026, 2, 2), 450L, 450L, 1L, 1L};
        Object[] tuesday = new Object[]{1, LocalDate.of(2026, 2, 3), 900L, 900L, 2L, 2L};

        List<Object[]> mockResults = Arrays.asList(monday, tuesday);
        when(scheduleEntryRepository.getDailyTotals(5, 2026))
                .thenReturn(mockResults);

        // Act
        List<DailyTotalDTO> results = scheduleService.getDailyTotals(5, 2026);

        // Assert
        assertEquals(2, results.size());
        assertEquals("Montag", results.get(0).getDayName());
        assertEquals("Dienstag", results.get(1).getDayName());
    }

    @Test
    void getDailyTotals_WithZeroMinutes_ShouldFormatAsZero() {
        // Arrange
        Object[] row = new Object[]{0, LocalDate.of(2026, 2, 2), 0L, 0L, 0L, 0L};

        when(scheduleEntryRepository.getDailyTotals(5, 2026))
                .thenReturn(Collections.singletonList(row));

        // Act
        List<DailyTotalDTO> results = scheduleService.getDailyTotals(5, 2026);

        // Assert
        assertEquals("0:00", results.get(0).getHoursWithoutPraktikanten());
        assertEquals("0:00", results.get(0).getHoursWithPraktikanten());
    }

    @Test
    void createScheduleEntry_ShouldSaveAndReturnDTO() {
        // Arrange
        ScheduleEntry newEntry = TestDataBuilder.createTestScheduleEntry(
            testWeeklySchedule, testStaff, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));

        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenReturn(newEntry);

        // Act
        ScheduleEntryDTO result = scheduleService.createScheduleEntry(newEntry);

        // Assert
        assertNotNull(result);
        assertEquals(newEntry.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(testStaff.getFullName(), result.getStaffName());

        verify(scheduleEntryRepository, times(1)).save(newEntry);
    }

    @Test
    void updateScheduleEntry_ShouldUpdateFields() {
        // Arrange
        testEntry.setStartTime(LocalTime.of(8, 0));
        testEntry.setEndTime(LocalTime.of(16, 0));
        testEntry.setStatus("normal");

        ScheduleEntry updateData = new ScheduleEntry();
        updateData.setStartTime(LocalTime.of(9, 0));
        updateData.setEndTime(LocalTime.of(17, 0));
        updateData.setStatus("krank");
        updateData.setNotes("Updated notes");

        when(scheduleEntryRepository.findById(1L))
                .thenReturn(Optional.of(testEntry));
        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenReturn(testEntry);

        // Act
        ScheduleEntryDTO result = scheduleService.updateScheduleEntry(1L, updateData);

        // Assert
        assertNotNull(result);
        verify(scheduleEntryRepository, times(1)).findById(1L);
        verify(scheduleEntryRepository, times(1)).save(testEntry);

        // Verify fields were updated
        assertEquals(LocalTime.of(9, 0), testEntry.getStartTime());
        assertEquals(LocalTime.of(17, 0), testEntry.getEndTime());
        assertEquals("krank", testEntry.getStatus());
        assertEquals("Updated notes", testEntry.getNotes());
    }

    @Test
    void updateScheduleEntry_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        testEntry.setStartTime(LocalTime.of(8, 0));
        testEntry.setEndTime(LocalTime.of(16, 0));
        testEntry.setStatus("normal");
        testEntry.setNotes("Original notes");

        ScheduleEntry updateData = new ScheduleEntry();
        updateData.setStatus("krank"); // Only update status

        when(scheduleEntryRepository.findById(1L))
                .thenReturn(Optional.of(testEntry));
        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenReturn(testEntry);

        // Act
        scheduleService.updateScheduleEntry(1L, updateData);

        // Assert
        assertEquals("krank", testEntry.getStatus());
        assertEquals(LocalTime.of(8, 0), testEntry.getStartTime()); // Unchanged
        assertEquals(LocalTime.of(16, 0), testEntry.getEndTime()); // Unchanged
        assertEquals("Original notes", testEntry.getNotes()); // Unchanged
    }

    @Test
    void updateScheduleEntry_NotFound_ShouldThrowException() {
        // Arrange
        when(scheduleEntryRepository.findById(999L))
                .thenReturn(Optional.empty());

        ScheduleEntry updateData = new ScheduleEntry();

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            scheduleService.updateScheduleEntry(999L, updateData);
        });

        assertEquals("Schedule entry not found", exception.getMessage());
        verify(scheduleEntryRepository, never()).save(any());
    }

    @Test
    void deleteScheduleEntry_ShouldCallRepository() {
        // Arrange
        doNothing().when(scheduleEntryRepository).deleteById(1L);

        // Act
        scheduleService.deleteScheduleEntry(1L);

        // Assert
        verify(scheduleEntryRepository, times(1)).deleteById(1L);
    }

    @Test
    void convertToDTO_ShouldMapAllFields() {
        // Arrange - Entry with all fields populated
        testEntry.setStartTime(LocalTime.of(8, 0));
        testEntry.setEndTime(LocalTime.of(16, 0));
        testEntry.setStatus("normal");
        testEntry.setWorkingHoursMinutes(450);
        testEntry.setBreakMinutes(30);
        testEntry.setNotes("Test notes");
        testEntry.calculateWorkingHours();

        when(scheduleEntryRepository.findByWeekNumberAndYear(5, 2026))
                .thenReturn(Arrays.asList(testEntry));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForWeek(5, 2026);

        // Assert
        ScheduleEntryDTO dto = results.get(0);
        assertEquals(testEntry.getId(), dto.getId());
        assertEquals(testWeeklySchedule.getId(), dto.getWeeklyScheduleId());
        assertEquals(testStaff.getId(), dto.getStaffId());
        assertEquals(testStaff.getFullName(), dto.getStaffName());
        assertEquals(testStaff.getRole(), dto.getStaffRole());
        assertEquals(testGroup.getName(), dto.getGroupName());
        assertEquals(testEntry.getDayOfWeek(), dto.getDayOfWeek());
        assertEquals(testEntry.getWorkDate(), dto.getWorkDate());
        assertEquals(testEntry.getStartTime(), dto.getStartTime());
        assertEquals(testEntry.getEndTime(), dto.getEndTime());
        assertEquals(testEntry.getStatus(), dto.getStatus());
        assertEquals(testEntry.getWorkingHoursMinutes(), dto.getWorkingHoursMinutes());
        assertEquals(testEntry.getBreakMinutes(), dto.getBreakMinutes());
        assertEquals(testEntry.getNotes(), dto.getNotes());
        assertEquals("7:30", dto.getWorkingHoursFormatted());
        assertEquals("0:30", dto.getBreakTimeFormatted());
    }

    @Test
    void convertToDTO_WithNullGroup_ShouldHandleGracefully() {
        // Arrange - Staff without group
        Staff staffWithoutGroup = TestDataBuilder.createTestStaff("Anna", "Schmidt", null);
        staffWithoutGroup.setId(2L);

        ScheduleEntry entryWithoutGroup = TestDataBuilder.createTestScheduleEntry(
            testWeeklySchedule, staffWithoutGroup);
        entryWithoutGroup.setId(2L);

        when(scheduleEntryRepository.findByWeekNumberAndYear(5, 2026))
                .thenReturn(Arrays.asList(entryWithoutGroup));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForWeek(5, 2026);

        // Assert
        ScheduleEntryDTO dto = results.get(0);
        assertNull(dto.getGroupName()); // Should not throw NPE
        assertEquals("Anna Schmidt", dto.getStaffName());
    }

    @Test
    void getScheduleForWeek_WithMultipleEntries_ShouldConvertAll() {
        // Arrange
        Staff staff2 = TestDataBuilder.createTestStaff("Lisa", "Schmidt", testGroup);
        staff2.setId(2L);

        ScheduleEntry entry2 = TestDataBuilder.createTestScheduleEntry(
            testWeeklySchedule, staff2, 1, LocalTime.of(9, 0), LocalTime.of(17, 0));
        entry2.setId(2L);

        when(scheduleEntryRepository.findByWeekNumberAndYear(5, 2026))
                .thenReturn(Arrays.asList(testEntry, entry2));

        // Act
        List<ScheduleEntryDTO> results = scheduleService.getScheduleForWeek(5, 2026);

        // Assert
        assertEquals(2, results.size());
        assertEquals("Max Mustermann", results.get(0).getStaffName());
        assertEquals("Lisa Schmidt", results.get(1).getStaffName());
    }

    @Test
    void createScheduleEntry_ShouldPreserveAllData() {
        // Arrange
        ScheduleEntry newEntry = new ScheduleEntry();
        newEntry.setWeeklySchedule(testWeeklySchedule);
        newEntry.setStaff(testStaff);
        newEntry.setDayOfWeek(2);
        newEntry.setWorkDate(LocalDate.of(2026, 2, 4));
        newEntry.setStartTime(LocalTime.of(10, 0));
        newEntry.setEndTime(LocalTime.of(18, 0));
        newEntry.setStatus("normal");
        newEntry.setNotes("New entry notes");

        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenReturn(newEntry);

        // Act
        ScheduleEntryDTO result = scheduleService.createScheduleEntry(newEntry);

        // Assert
        assertEquals(2, result.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 4), result.getWorkDate());
        assertEquals(LocalTime.of(10, 0), result.getStartTime());
        assertEquals(LocalTime.of(18, 0), result.getEndTime());
        assertEquals("normal", result.getStatus());
        assertEquals("New entry notes", result.getNotes());
    }

    @Test
    void getDailyTotals_ShouldHandleAllDaysOfWeek() {
        // Arrange - Test all 7 days
        Object[] monday = new Object[]{0, LocalDate.of(2026, 2, 2), 450L, 450L, 1L, 1L};
        Object[] tuesday = new Object[]{1, LocalDate.of(2026, 2, 3), 450L, 450L, 1L, 1L};
        Object[] wednesday = new Object[]{2, LocalDate.of(2026, 2, 4), 450L, 450L, 1L, 1L};
        Object[] thursday = new Object[]{3, LocalDate.of(2026, 2, 5), 450L, 450L, 1L, 1L};
        Object[] friday = new Object[]{4, LocalDate.of(2026, 2, 6), 450L, 450L, 1L, 1L};

        List<Object[]> mockResults = Arrays.asList(monday, tuesday, wednesday, thursday, friday);
        when(scheduleEntryRepository.getDailyTotals(5, 2026))
                .thenReturn(mockResults);

        // Act
        List<DailyTotalDTO> results = scheduleService.getDailyTotals(5, 2026);

        // Assert
        assertEquals(5, results.size());
        assertEquals("Montag", results.get(0).getDayName());
        assertEquals("Dienstag", results.get(1).getDayName());
        assertEquals("Mittwoch", results.get(2).getDayName());
        assertEquals("Donnerstag", results.get(3).getDayName());
        assertEquals("Freitag", results.get(4).getDayName());
    }

    @Test
    void updateScheduleEntry_ShouldTriggerRecalculation() {
        // Arrange - Entry with initial times
        testEntry.setStartTime(LocalTime.of(8, 0));
        testEntry.setEndTime(LocalTime.of(14, 0));
        testEntry.setStatus("normal");
        testEntry.calculateWorkingHours(); // 6 hours = 360 min (no break)

        assertEquals(360, testEntry.getWorkingHoursMinutes());

        ScheduleEntry updateData = new ScheduleEntry();
        updateData.setEndTime(LocalTime.of(16, 0)); // Change to 8 hours

        when(scheduleEntryRepository.findById(1L))
                .thenReturn(Optional.of(testEntry));
        when(scheduleEntryRepository.save(any(ScheduleEntry.class)))
                .thenAnswer(invocation -> {
                    ScheduleEntry saved = invocation.getArgument(0);
                    saved.calculateWorkingHours(); // Simulate @PreUpdate
                    return saved;
                });

        // Act
        ScheduleEntryDTO result = scheduleService.updateScheduleEntry(1L, updateData);

        // Assert
        // After update: 8:00-16:00 = 8 hours = 480 min - 30 min break = 450 min
        assertEquals(450, testEntry.getWorkingHoursMinutes());
        assertEquals(450, result.getWorkingHoursMinutes());
    }
}
