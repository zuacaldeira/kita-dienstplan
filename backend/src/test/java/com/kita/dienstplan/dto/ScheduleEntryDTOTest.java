package com.kita.dienstplan.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScheduleEntryDTO
 * Tests formatting methods and static helpers
 */
class ScheduleEntryDTOTest {

    private ScheduleEntryDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ScheduleEntryDTO();
    }

    @Test
    void formatMinutes_WithValidMinutes_ShouldFormatCorrectly() {
        // Act & Assert
        assertEquals("7:30", ScheduleEntryDTO.formatMinutes(450));
        assertEquals("8:00", ScheduleEntryDTO.formatMinutes(480));
        assertEquals("15:00", ScheduleEntryDTO.formatMinutes(900));
    }

    @Test
    void formatMinutes_WithZero_ShouldReturnZero() {
        // Act & Assert
        assertEquals("0:00", ScheduleEntryDTO.formatMinutes(0));
    }

    @Test
    void formatMinutes_WithNull_ShouldReturnZero() {
        // Act & Assert
        assertEquals("0:00", ScheduleEntryDTO.formatMinutes(null));
    }

    @Test
    void formatMinutes_WithSingleDigitMinutes_ShouldPadWithZero() {
        // Act & Assert
        assertEquals("1:05", ScheduleEntryDTO.formatMinutes(65));
        assertEquals("2:05", ScheduleEntryDTO.formatMinutes(125));
        assertEquals("0:09", ScheduleEntryDTO.formatMinutes(9));
    }

    @Test
    void formatMinutes_WithOnlyMinutes_ShouldFormatCorrectly() {
        // Act & Assert
        assertEquals("0:45", ScheduleEntryDTO.formatMinutes(45));
        assertEquals("0:30", ScheduleEntryDTO.formatMinutes(30));
        assertEquals("0:15", ScheduleEntryDTO.formatMinutes(15));
    }

    @Test
    void formatMinutes_WithExactHours_ShouldFormatCorrectly() {
        // Act & Assert
        assertEquals("1:00", ScheduleEntryDTO.formatMinutes(60));
        assertEquals("8:00", ScheduleEntryDTO.formatMinutes(480));
        assertEquals("10:00", ScheduleEntryDTO.formatMinutes(600));
    }

    @Test
    void formatMinutes_WithLargeHours_ShouldFormatCorrectly() {
        // Act & Assert
        assertEquals("25:00", ScheduleEntryDTO.formatMinutes(1500));
        assertEquals("50:30", ScheduleEntryDTO.formatMinutes(3030));
    }

    @Test
    void setWorkingHoursFormatted_WithValidMinutes_ShouldSetFormatted() {
        // Arrange
        dto.setWorkingHoursMinutes(450);

        // Act
        dto.setWorkingHoursFormatted();

        // Assert
        assertEquals("7:30", dto.getWorkingHoursFormatted());
    }

    @Test
    void setWorkingHoursFormatted_WithZeroMinutes_ShouldSetZero() {
        // Arrange
        dto.setWorkingHoursMinutes(0);

        // Act
        dto.setWorkingHoursFormatted();

        // Assert
        assertEquals("0:00", dto.getWorkingHoursFormatted());
    }

    @Test
    void setWorkingHoursFormatted_WithNullMinutes_ShouldSetZero() {
        // Arrange
        dto.setWorkingHoursMinutes(null);

        // Act
        dto.setWorkingHoursFormatted();

        // Assert
        assertEquals("0:00", dto.getWorkingHoursFormatted());
    }

    @Test
    void setBreakTimeFormatted_WithValidMinutes_ShouldSetFormatted() {
        // Arrange
        dto.setBreakMinutes(30);

        // Act
        dto.setBreakTimeFormatted();

        // Assert
        assertEquals("0:30", dto.getBreakTimeFormatted());
    }

    @Test
    void setBreakTimeFormatted_WithZeroMinutes_ShouldSetZero() {
        // Arrange
        dto.setBreakMinutes(0);

        // Act
        dto.setBreakTimeFormatted();

        // Assert
        assertEquals("0:00", dto.getBreakTimeFormatted());
    }

    @Test
    void setBreakTimeFormatted_WithNullMinutes_ShouldSetZero() {
        // Arrange
        dto.setBreakMinutes(null);

        // Act
        dto.setBreakTimeFormatted();

        // Assert
        assertEquals("0:00", dto.getBreakTimeFormatted());
    }

    @Test
    void setBothFormattedMethods_ShouldWorkIndependently() {
        // Arrange
        dto.setWorkingHoursMinutes(450);
        dto.setBreakMinutes(30);

        // Act
        dto.setWorkingHoursFormatted();
        dto.setBreakTimeFormatted();

        // Assert
        assertEquals("7:30", dto.getWorkingHoursFormatted());
        assertEquals("0:30", dto.getBreakTimeFormatted());
    }

    @Test
    void constructor_WithAllArguments_ShouldSetAllFields() {
        // Arrange & Act
        ScheduleEntryDTO fullDto = new ScheduleEntryDTO(
            1L,
            10L,
            20L,
            "Max Mustermann",
            "Erzieher",
            "Käfer",
            0,
            LocalDate.of(2026, 2, 2),
            LocalTime.of(8, 0),
            LocalTime.of(16, 0),
            "normal",
            450,
            30,
            "7:30",
            "0:30",
            "Test notes"
        );

        // Assert
        assertEquals(1L, fullDto.getId());
        assertEquals(10L, fullDto.getWeeklyScheduleId());
        assertEquals(20L, fullDto.getStaffId());
        assertEquals("Max Mustermann", fullDto.getStaffName());
        assertEquals("Erzieher", fullDto.getStaffRole());
        assertEquals("Käfer", fullDto.getGroupName());
        assertEquals(0, fullDto.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 2), fullDto.getWorkDate());
        assertEquals(LocalTime.of(8, 0), fullDto.getStartTime());
        assertEquals(LocalTime.of(16, 0), fullDto.getEndTime());
        assertEquals("normal", fullDto.getStatus());
        assertEquals(450, fullDto.getWorkingHoursMinutes());
        assertEquals(30, fullDto.getBreakMinutes());
        assertEquals("7:30", fullDto.getWorkingHoursFormatted());
        assertEquals("0:30", fullDto.getBreakTimeFormatted());
        assertEquals("Test notes", fullDto.getNotes());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        // Arrange & Act
        ScheduleEntryDTO emptyDto = new ScheduleEntryDTO();

        // Assert
        assertNull(emptyDto.getId());
        assertNull(emptyDto.getWeeklyScheduleId());
        assertNull(emptyDto.getStaffId());
        assertNull(emptyDto.getStaffName());
        assertNull(emptyDto.getStaffRole());
        assertNull(emptyDto.getGroupName());
        assertNull(emptyDto.getDayOfWeek());
        assertNull(emptyDto.getWorkDate());
        assertNull(emptyDto.getStartTime());
        assertNull(emptyDto.getEndTime());
        assertNull(emptyDto.getStatus());
        assertNull(emptyDto.getWorkingHoursMinutes());
        assertNull(emptyDto.getBreakMinutes());
        assertNull(emptyDto.getWorkingHoursFormatted());
        assertNull(emptyDto.getBreakTimeFormatted());
        assertNull(emptyDto.getNotes());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange & Act
        dto.setId(1L);
        dto.setWeeklyScheduleId(10L);
        dto.setStaffId(20L);
        dto.setStaffName("Anna Schmidt");
        dto.setStaffRole("Praktikant");
        dto.setGroupName("Bienen");
        dto.setDayOfWeek(2);
        dto.setWorkDate(LocalDate.of(2026, 2, 4));
        dto.setStartTime(LocalTime.of(9, 0));
        dto.setEndTime(LocalTime.of(17, 0));
        dto.setStatus("normal");
        dto.setWorkingHoursMinutes(450);
        dto.setBreakMinutes(30);
        dto.setNotes("Wednesday shift");

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getWeeklyScheduleId());
        assertEquals(20L, dto.getStaffId());
        assertEquals("Anna Schmidt", dto.getStaffName());
        assertEquals("Praktikant", dto.getStaffRole());
        assertEquals("Bienen", dto.getGroupName());
        assertEquals(2, dto.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 4), dto.getWorkDate());
        assertEquals(LocalTime.of(9, 0), dto.getStartTime());
        assertEquals(LocalTime.of(17, 0), dto.getEndTime());
        assertEquals("normal", dto.getStatus());
        assertEquals(450, dto.getWorkingHoursMinutes());
        assertEquals(30, dto.getBreakMinutes());
        assertEquals("Wednesday shift", dto.getNotes());
    }

    @Test
    void formatMinutes_WithVariousMinuteValues_ShouldFormatConsistently() {
        // Test a range of minute values
        assertEquals("0:01", ScheduleEntryDTO.formatMinutes(1));
        assertEquals("0:10", ScheduleEntryDTO.formatMinutes(10));
        assertEquals("0:59", ScheduleEntryDTO.formatMinutes(59));
        assertEquals("1:00", ScheduleEntryDTO.formatMinutes(60));
        assertEquals("1:01", ScheduleEntryDTO.formatMinutes(61));
        assertEquals("5:45", ScheduleEntryDTO.formatMinutes(345));
        assertEquals("12:15", ScheduleEntryDTO.formatMinutes(735));
    }

    @Test
    void setWorkingHoursFormatted_CalledMultipleTimes_ShouldRecalculate() {
        // Arrange
        dto.setWorkingHoursMinutes(450);
        dto.setWorkingHoursFormatted();
        assertEquals("7:30", dto.getWorkingHoursFormatted());

        // Act - Change minutes and recalculate
        dto.setWorkingHoursMinutes(900);
        dto.setWorkingHoursFormatted();

        // Assert
        assertEquals("15:00", dto.getWorkingHoursFormatted());
    }

    @Test
    void setBreakTimeFormatted_CalledMultipleTimes_ShouldRecalculate() {
        // Arrange
        dto.setBreakMinutes(30);
        dto.setBreakTimeFormatted();
        assertEquals("0:30", dto.getBreakTimeFormatted());

        // Act - Change minutes and recalculate
        dto.setBreakMinutes(60);
        dto.setBreakTimeFormatted();

        // Assert
        assertEquals("1:00", dto.getBreakTimeFormatted());
    }

    @Test
    void formatMinutes_IsStaticMethod_ShouldWorkWithoutInstance() {
        // Act & Assert - Call static method directly without instance
        String result = ScheduleEntryDTO.formatMinutes(450);

        // Assert
        assertEquals("7:30", result);
        assertNotNull(result);
    }
}
