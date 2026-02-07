package com.kita.dienstplan.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeeklyStaffTotalDTO
 * Tests formatting methods and constructor validation
 *
 * Note: WeeklyStaffTotalDTO is package-private, so we test it from the same package
 */
class WeeklyStaffTotalDTOTest {

    private WeeklyStaffTotalDTO dto;

    @BeforeEach
    void setUp() {
        dto = new WeeklyStaffTotalDTO();
    }

    @Test
    void setFormattedTotals_WithValidMinutes_ShouldFormatBothFields() {
        // Arrange
        dto.setTotalWorkingMinutes(450);
        dto.setTotalBreakMinutes(30);

        // Act
        dto.setFormattedTotals();

        // Assert
        assertEquals("7:30", dto.getTotalHoursFormatted());
        assertEquals("0:30", dto.getTotalBreakFormatted());
    }

    @Test
    void setFormattedTotals_WithZeroMinutes_ShouldFormatAsZero() {
        // Arrange
        dto.setTotalWorkingMinutes(0);
        dto.setTotalBreakMinutes(0);

        // Act
        dto.setFormattedTotals();

        // Assert
        assertEquals("0:00", dto.getTotalHoursFormatted());
        assertEquals("0:00", dto.getTotalBreakFormatted());
    }

    @Test
    void setFormattedTotals_WithNullMinutes_ShouldFormatAsZero() {
        // Arrange
        dto.setTotalWorkingMinutes(null);
        dto.setTotalBreakMinutes(null);

        // Act
        dto.setFormattedTotals();

        // Assert
        assertEquals("0:00", dto.getTotalHoursFormatted());
        assertEquals("0:00", dto.getTotalBreakFormatted());
    }

    @Test
    void constructor_WithAllArguments_ShouldSetAllFields() {
        // Arrange & Act
        WeeklyStaffTotalDTO fullDto = new WeeklyStaffTotalDTO(
            1L,
            "Max Mustermann",
            "Erzieher",
            "Käfer",
            2250,  // 37.5 hours
            150,   // 2.5 hours break
            "37:30",
            "2:30",
            5L,
            0L,
            2L,
            0L
        );

        // Assert
        assertEquals(1L, fullDto.getStaffId());
        assertEquals("Max Mustermann", fullDto.getFullName());
        assertEquals("Erzieher", fullDto.getRole());
        assertEquals("Käfer", fullDto.getGroupName());
        assertEquals(2250, fullDto.getTotalWorkingMinutes());
        assertEquals(150, fullDto.getTotalBreakMinutes());
        assertEquals("37:30", fullDto.getTotalHoursFormatted());
        assertEquals("2:30", fullDto.getTotalBreakFormatted());
        assertEquals(5L, fullDto.getDaysWorked());
        assertEquals(0L, fullDto.getDaysSick());
        assertEquals(2L, fullDto.getDaysOff());
        assertEquals(0L, fullDto.getSchoolDays());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        // Arrange & Act
        WeeklyStaffTotalDTO emptyDto = new WeeklyStaffTotalDTO();

        // Assert
        assertNull(emptyDto.getStaffId());
        assertNull(emptyDto.getFullName());
        assertNull(emptyDto.getRole());
        assertNull(emptyDto.getGroupName());
        assertNull(emptyDto.getTotalWorkingMinutes());
        assertNull(emptyDto.getTotalBreakMinutes());
        assertNull(emptyDto.getTotalHoursFormatted());
        assertNull(emptyDto.getTotalBreakFormatted());
        assertNull(emptyDto.getDaysWorked());
        assertNull(emptyDto.getDaysSick());
        assertNull(emptyDto.getDaysOff());
        assertNull(emptyDto.getSchoolDays());
    }
}
