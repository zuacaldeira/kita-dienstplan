package com.kita.dienstplan.controller;

import com.kita.dienstplan.dto.DailyTotalDTO;
import com.kita.dienstplan.dto.ScheduleEntryDTO;
import com.kita.dienstplan.entity.ScheduleEntry;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.StaffRepository;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import com.kita.dienstplan.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST Controller for schedule operations
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for development
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final StaffRepository staffRepository;

    /**
     * GET /api/schedules/week/{year}/{week}
     * Get all schedule entries for a specific week
     */
    @GetMapping("/week/{year}/{week}")
    public ResponseEntity<List<ScheduleEntryDTO>> getScheduleForWeek(
            @PathVariable Integer year,
            @PathVariable Integer week) {
        List<ScheduleEntryDTO> entries = scheduleService.getScheduleForWeek(week, year);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/schedules/staff/{staffId}/week/{year}/{week}
     * Get schedule for a specific staff member in a week
     */
    @GetMapping("/staff/{staffId}/week/{year}/{week}")
    public ResponseEntity<List<ScheduleEntryDTO>> getScheduleForStaffInWeek(
            @PathVariable Long staffId,
            @PathVariable Integer year,
            @PathVariable Integer week) {
        List<ScheduleEntryDTO> entries = scheduleService.getScheduleForStaffInWeek(staffId, week, year);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/schedules/date/{date}
     * Get all schedule entries for a specific date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ScheduleEntryDTO>> getScheduleForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ScheduleEntryDTO> entries = scheduleService.getScheduleForDate(date);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/schedules/on-duty?date=2026-02-02&time=10:00
     * Find who is working at a specific date and time
     */
    @GetMapping("/on-duty")
    public ResponseEntity<List<ScheduleEntryDTO>> getWhoIsWorkingAt(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        List<ScheduleEntryDTO> entries = scheduleService.getWhoIsWorkingAt(date, time);
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/schedules/daily-totals/{year}/{week}
     * Get daily totals for a week
     */
    @GetMapping("/daily-totals/{year}/{week}")
    public ResponseEntity<List<DailyTotalDTO>> getDailyTotals(
            @PathVariable Integer year,
            @PathVariable Integer week) {
        List<DailyTotalDTO> totals = scheduleService.getDailyTotals(week, year);
        return ResponseEntity.ok(totals);
    }

    /**
     * POST /api/schedules/entries
     * Create a new schedule entry
     */
    @PostMapping("/entries")
    public ResponseEntity<ScheduleEntryDTO> createScheduleEntry(
            @RequestBody CreateScheduleEntryRequest request) {
        
        // Validate and fetch related entities
        WeeklySchedule weeklySchedule = weeklyScheduleRepository.findById(request.getWeeklyScheduleId())
                .orElseThrow(() -> new RuntimeException("Weekly schedule not found"));
        
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Create entry
        ScheduleEntry entry = new ScheduleEntry();
        entry.setWeeklySchedule(weeklySchedule);
        entry.setStaff(staff);
        entry.setDayOfWeek(request.getDayOfWeek());
        entry.setWorkDate(request.getWorkDate());
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());
        entry.setStatus(request.getStatus());
        entry.setNotes(request.getNotes());

        ScheduleEntryDTO created = scheduleService.createScheduleEntry(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/schedules/entries/{id}
     * Update an existing schedule entry
     */
    @PutMapping("/entries/{id}")
    public ResponseEntity<ScheduleEntryDTO> updateScheduleEntry(
            @PathVariable Long id,
            @RequestBody UpdateScheduleEntryRequest request) {
        
        ScheduleEntry entry = new ScheduleEntry();
        entry.setStartTime(request.getStartTime());
        entry.setEndTime(request.getEndTime());
        entry.setStatus(request.getStatus());
        entry.setNotes(request.getNotes());

        ScheduleEntryDTO updated = scheduleService.updateScheduleEntry(id, entry);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/schedules/entries/{id}
     * Delete a schedule entry
     */
    @DeleteMapping("/entries/{id}")
    public ResponseEntity<Void> deleteScheduleEntry(@PathVariable Long id) {
        scheduleService.deleteScheduleEntry(id);
        return ResponseEntity.noContent().build();
    }
}

// Request DTOs
@lombok.Data
class CreateScheduleEntryRequest {
    private Long weeklyScheduleId;
    private Long staffId;
    private Integer dayOfWeek;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
}

@lombok.Data
class UpdateScheduleEntryRequest {
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
}
