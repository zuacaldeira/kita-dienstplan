package com.kita.dienstplan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for WeeklySchedule entity
 * Used to avoid circular reference issues when serializing to JSON
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyScheduleDTO {
    private Long id;
    private Integer weekNumber;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;

    /**
     * Create DTO from entity
     */
    public static WeeklyScheduleDTO fromEntity(com.kita.dienstplan.entity.WeeklySchedule entity) {
        if (entity == null) return null;

        WeeklyScheduleDTO dto = new WeeklyScheduleDTO();
        dto.setId(entity.getId());
        dto.setWeekNumber(entity.getWeekNumber());
        dto.setYear(entity.getYear());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setNotes(entity.getNotes());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}
