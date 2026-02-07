package com.kita.dienstplan.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Entity representing a schedule entry (shift) for a staff member
 * Working hours and breaks are auto-calculated before save/update
 * Audit fields track which admin created/modified the entry
 */
@Entity
@Table(name = "schedule_entries",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"weekly_schedule_id", "staff_id", "day_of_week"}
       ))
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_schedule_id", nullable = false)
    @JsonIgnoreProperties("scheduleEntries")
    private WeeklySchedule weeklySchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    @JsonIgnoreProperties({"scheduleEntries", "group"})
    private Staff staff;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek; // 0=Monday, 6=Sunday

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(length = 50)
    private String status = "normal"; // normal, frei, krank, Schule, Fachschule, Urlaub, Feiertag

    @Column(name = "working_hours_minutes")
    private Integer workingHoursMinutes = 0;

    @Column(name = "break_minutes")
    private Integer breakMinutes = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Auto-calculate working hours and breaks before persisting
     */
    @PrePersist
    @PreUpdate
    public void calculateWorkingHours() {
        if (!"normal".equalsIgnoreCase(status) || startTime == null || endTime == null) {
            workingHoursMinutes = 0;
            breakMinutes = 0;
            return;
        }

        // Calculate total minutes
        long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        
        // Handle overnight shifts
        if (totalMinutes < 0) {
            totalMinutes += 24 * 60;
        }

        // Calculate break (30 minutes if > 6 hours)
        double totalHours = totalMinutes / 60.0;
        if (totalHours > 6) {
            breakMinutes = 30;
        } else {
            breakMinutes = 0;
        }

        // Working minutes = total - break
        workingHoursMinutes = (int) (totalMinutes - breakMinutes);
    }

    /**
     * Get formatted working hours as H:MM
     */
    @Transient
    public String getFormattedWorkingHours() {
        int hours = workingHoursMinutes / 60;
        int minutes = workingHoursMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }

    /**
     * Get formatted break time as H:MM
     */
    @Transient
    public String getFormattedBreakTime() {
        int hours = breakMinutes / 60;
        int minutes = breakMinutes % 60;
        return String.format("%d:%02d", hours, minutes);
    }
}
