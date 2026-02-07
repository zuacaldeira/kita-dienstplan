package com.kita.dienstplan.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DailyTotalDTO
 * Tests formatting methods and day name mapping
 */
class DailyTotalDTOTest {

    private DailyTotalDTO dto;

    @BeforeEach
    void setUp() {
        dto = new DailyTotalDTO();
    }

    @Test
    void setDayNameFromNumber_WithMonday_ShouldSetMontag() {
        // Arrange
        dto.setDayOfWeek(0);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Montag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithTuesday_ShouldSetDienstag() {
        // Arrange
        dto.setDayOfWeek(1);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Dienstag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithWednesday_ShouldSetMittwoch() {
        // Arrange
        dto.setDayOfWeek(2);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Mittwoch", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithThursday_ShouldSetDonnerstag() {
        // Arrange
        dto.setDayOfWeek(3);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Donnerstag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithFriday_ShouldSetFreitag() {
        // Arrange
        dto.setDayOfWeek(4);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Freitag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithSaturday_ShouldSetSamstag() {
        // Arrange
        dto.setDayOfWeek(5);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Samstag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithSunday_ShouldSetSonntag() {
        // Arrange
        dto.setDayOfWeek(6);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertEquals("Sonntag", dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithNullDayOfWeek_ShouldNotSetDayName() {
        // Arrange
        dto.setDayOfWeek(null);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertNull(dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithNegativeDayOfWeek_ShouldNotSetDayName() {
        // Arrange
        dto.setDayOfWeek(-1);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertNull(dto.getDayName());
    }

    @Test
    void setDayNameFromNumber_WithDayOfWeekGreaterThan6_ShouldNotSetDayName() {
        // Arrange
        dto.setDayOfWeek(7);

        // Act
        dto.setDayNameFromNumber();

        // Assert
        assertNull(dto.getDayName());
    }

    @Test
    void setFormattedHours_WithValidMinutes_ShouldFormatCorrectly() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(450); // 7 hours 30 minutes
        dto.setTotalMinutesWithPraktikanten(900);     // 15 hours

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("7:30", dto.getHoursWithoutPraktikanten());
        assertEquals("15:00", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithZeroMinutes_ShouldReturnZero() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(0);
        dto.setTotalMinutesWithPraktikanten(0);

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("0:00", dto.getHoursWithoutPraktikanten());
        assertEquals("0:00", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithNullMinutes_ShouldReturnZero() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(null);
        dto.setTotalMinutesWithPraktikanten(null);

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("0:00", dto.getHoursWithoutPraktikanten());
        assertEquals("0:00", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithSingleDigitMinutes_ShouldPadWithZero() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(65); // 1 hour 5 minutes
        dto.setTotalMinutesWithPraktikanten(125);    // 2 hours 5 minutes

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("1:05", dto.getHoursWithoutPraktikanten());
        assertEquals("2:05", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithOnlyMinutes_ShouldFormatCorrectly() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(45); // 0 hours 45 minutes
        dto.setTotalMinutesWithPraktikanten(30);     // 0 hours 30 minutes

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("0:45", dto.getHoursWithoutPraktikanten());
        assertEquals("0:30", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithExactHours_ShouldFormatCorrectly() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(480); // 8 hours exactly
        dto.setTotalMinutesWithPraktikanten(600);     // 10 hours exactly

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("8:00", dto.getHoursWithoutPraktikanten());
        assertEquals("10:00", dto.getHoursWithPraktikanten());
    }

    @Test
    void setFormattedHours_WithLargeHours_ShouldFormatCorrectly() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(1500); // 25 hours
        dto.setTotalMinutesWithPraktikanten(3000);     // 50 hours

        // Act
        dto.setFormattedHours();

        // Assert
        assertEquals("25:00", dto.getHoursWithoutPraktikanten());
        assertEquals("50:00", dto.getHoursWithPraktikanten());
    }

    @Test
    void constructor_WithAllArguments_ShouldSetAllFields() {
        // Arrange & Act
        DailyTotalDTO fullDto = new DailyTotalDTO(
            0,
            LocalDate.of(2026, 2, 2),
            "Montag",
            900,
            1200,
            "15:00",
            "20:00",
            2L,
            3L
        );

        // Assert
        assertEquals(0, fullDto.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 2), fullDto.getWorkDate());
        assertEquals("Montag", fullDto.getDayName());
        assertEquals(900, fullDto.getTotalMinutesWithoutPraktikanten());
        assertEquals(1200, fullDto.getTotalMinutesWithPraktikanten());
        assertEquals("15:00", fullDto.getHoursWithoutPraktikanten());
        assertEquals("20:00", fullDto.getHoursWithPraktikanten());
        assertEquals(2L, fullDto.getStaffCountWithoutPraktikanten());
        assertEquals(3L, fullDto.getTotalStaffCount());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        // Arrange & Act
        DailyTotalDTO emptyDto = new DailyTotalDTO();

        // Assert
        assertNull(emptyDto.getDayOfWeek());
        assertNull(emptyDto.getWorkDate());
        assertNull(emptyDto.getDayName());
        assertNull(emptyDto.getTotalMinutesWithoutPraktikanten());
        assertNull(emptyDto.getTotalMinutesWithPraktikanten());
        assertNull(emptyDto.getHoursWithoutPraktikanten());
        assertNull(emptyDto.getHoursWithPraktikanten());
        assertNull(emptyDto.getStaffCountWithoutPraktikanten());
        assertNull(emptyDto.getTotalStaffCount());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange & Act
        dto.setDayOfWeek(1);
        dto.setWorkDate(LocalDate.of(2026, 2, 3));
        dto.setTotalMinutesWithoutPraktikanten(450);
        dto.setTotalMinutesWithPraktikanten(600);
        dto.setStaffCountWithoutPraktikanten(3L);
        dto.setTotalStaffCount(4L);

        // Assert
        assertEquals(1, dto.getDayOfWeek());
        assertEquals(LocalDate.of(2026, 2, 3), dto.getWorkDate());
        assertEquals(450, dto.getTotalMinutesWithoutPraktikanten());
        assertEquals(600, dto.getTotalMinutesWithPraktikanten());
        assertEquals(3L, dto.getStaffCountWithoutPraktikanten());
        assertEquals(4L, dto.getTotalStaffCount());
    }

    @Test
    void setFormattedHours_CalledMultipleTimes_ShouldRecalculate() {
        // Arrange
        dto.setTotalMinutesWithoutPraktikanten(450);
        dto.setFormattedHours();
        assertEquals("7:30", dto.getHoursWithoutPraktikanten());

        // Act - Change minutes and recalculate
        dto.setTotalMinutesWithoutPraktikanten(900);
        dto.setFormattedHours();

        // Assert
        assertEquals("15:00", dto.getHoursWithoutPraktikanten());
    }
}
