package com.kita.dienstplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for daily totals
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyTotalDTO {
    private Integer dayOfWeek;
    private LocalDate workDate;
    private String dayName;
    private Integer totalMinutesWithoutPraktikanten;
    private Integer totalMinutesWithPraktikanten;
    private String hoursWithoutPraktikanten;
    private String hoursWithPraktikanten;
    private Long staffCountWithoutPraktikanten;
    private Long totalStaffCount;

    public void setFormattedHours() {
        this.hoursWithoutPraktikanten = formatMinutes(totalMinutesWithoutPraktikanten);
        this.hoursWithPraktikanten = formatMinutes(totalMinutesWithPraktikanten);
    }

    public void setDayNameFromNumber() {
        String[] days = {"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};
        if (dayOfWeek != null && dayOfWeek >= 0 && dayOfWeek < days.length) {
            this.dayName = days[dayOfWeek];
        }
    }

    private String formatMinutes(Integer minutes) {
        if (minutes == null || minutes == 0) return "0:00";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }
}

/**
 * DTO for weekly staff totals
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class WeeklyStaffTotalDTO {
    private Long staffId;
    private String fullName;
    private String role;
    private String groupName;
    private Integer totalWorkingMinutes;
    private Integer totalBreakMinutes;
    private String totalHoursFormatted;
    private String totalBreakFormatted;
    private Long daysWorked;
    private Long daysSick;
    private Long daysOff;
    private Long schoolDays;

    public void setFormattedTotals() {
        this.totalHoursFormatted = formatMinutes(totalWorkingMinutes);
        this.totalBreakFormatted = formatMinutes(totalBreakMinutes);
    }

    private String formatMinutes(Integer minutes) {
        if (minutes == null || minutes == 0) return "0:00";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }
}
