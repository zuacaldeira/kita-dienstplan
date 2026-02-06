package com.kita.dienstplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for ScheduleEntry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntryDTO {
    private Long id;
    private Long weeklyScheduleId;
    private Long staffId;
    private String staffName;
    private String staffRole;
    private String groupName;
    private Integer dayOfWeek;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Integer workingHoursMinutes;
    private Integer breakMinutes;
    private String workingHoursFormatted;
    private String breakTimeFormatted;
    private String notes;

    /**
     * Convert minutes to formatted time
     */
    public static String formatMinutes(Integer minutes) {
        if (minutes == null || minutes == 0) return "0:00";
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }

    public void setWorkingHoursFormatted() {
        this.workingHoursFormatted = formatMinutes(workingHoursMinutes);
    }

    public void setBreakTimeFormatted() {
        this.breakTimeFormatted = formatMinutes(breakMinutes);
    }
}
