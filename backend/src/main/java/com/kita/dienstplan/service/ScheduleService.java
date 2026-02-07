package com.kita.dienstplan.service;

import com.kita.dienstplan.dto.DailyTotalDTO;
import com.kita.dienstplan.dto.ScheduleEntryDTO;
import com.kita.dienstplan.entity.ScheduleEntry;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.ScheduleEntryRepository;
import com.kita.dienstplan.repository.StaffRepository;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for schedule management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleEntryRepository scheduleEntryRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final StaffRepository staffRepository;

    /**
     * Get all schedule entries for a specific week
     */
    public List<ScheduleEntryDTO> getScheduleForWeek(Integer weekNumber, Integer year) {
        List<ScheduleEntry> entries = scheduleEntryRepository.findByWeekNumberAndYear(weekNumber, year);
        return entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get schedule entries for a specific staff member in a week
     */
    public List<ScheduleEntryDTO> getScheduleForStaffInWeek(Long staffId, Integer weekNumber, Integer year) {
        List<ScheduleEntry> entries = scheduleEntryRepository.findByStaffAndWeek(staffId, weekNumber, year);
        return entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get schedule entries for a specific date
     */
    public List<ScheduleEntryDTO> getScheduleForDate(LocalDate date) {
        List<ScheduleEntry> entries = scheduleEntryRepository.findByWorkDateOrderByStaff_FullName(date);
        return entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find who is working at a specific date and time
     */
    public List<ScheduleEntryDTO> getWhoIsWorkingAt(LocalDate date, LocalTime time) {
        List<ScheduleEntry> entries = scheduleEntryRepository.findWhoIsWorkingAt(date, time);
        return entries.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get daily totals for a week
     */
    public List<DailyTotalDTO> getDailyTotals(Integer weekNumber, Integer year) {
        List<Object[]> results = scheduleEntryRepository.getDailyTotals(weekNumber, year);
        
        return results.stream()
                .map(row -> {
                    DailyTotalDTO dto = new DailyTotalDTO();
                    dto.setDayOfWeek((Integer) row[0]);
                    dto.setWorkDate((LocalDate) row[1]);
                    dto.setTotalMinutesWithoutPraktikanten(((Number) row[2]).intValue());
                    dto.setTotalMinutesWithPraktikanten(((Number) row[3]).intValue());
                    dto.setStaffCountWithoutPraktikanten((Long) row[4]);
                    dto.setTotalStaffCount((Long) row[5]);
                    dto.setDayNameFromNumber();
                    dto.setFormattedHours();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Create a new schedule entry
     */
    @Transactional
    public ScheduleEntryDTO createScheduleEntry(ScheduleEntry entry) {
        // Validation happens in controller
        ScheduleEntry saved = scheduleEntryRepository.save(entry);
        // Flush to ensure data is persisted
        scheduleEntryRepository.flush();
        // Convert to DTO immediately within transaction
        ScheduleEntryDTO dto = convertToDTO(saved);
        return dto;
    }

    /**
     * Update an existing schedule entry
     */
    @Transactional
    public ScheduleEntryDTO updateScheduleEntry(Long id, ScheduleEntry updatedEntry) {
        ScheduleEntry existing = scheduleEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule entry not found"));

        // Update fields
        if (updatedEntry.getStartTime() != null) {
            existing.setStartTime(updatedEntry.getStartTime());
        }
        if (updatedEntry.getEndTime() != null) {
            existing.setEndTime(updatedEntry.getEndTime());
        }
        if (updatedEntry.getStatus() != null) {
            existing.setStatus(updatedEntry.getStatus());
        }
        if (updatedEntry.getNotes() != null) {
            existing.setNotes(updatedEntry.getNotes());
        }

        // Save and return (auto-calculation happens in @PreUpdate)
        ScheduleEntry saved = scheduleEntryRepository.save(existing);
        return convertToDTO(saved);
    }

    /**
     * Delete a schedule entry
     */
    @Transactional
    public void deleteScheduleEntry(Long id) {
        scheduleEntryRepository.deleteById(id);
    }

    /**
     * Convert entity to DTO
     */
    private ScheduleEntryDTO convertToDTO(ScheduleEntry entry) {
        ScheduleEntryDTO dto = new ScheduleEntryDTO();
        dto.setId(entry.getId());

        // Safely extract relationship IDs to avoid lazy loading issues
        try {
            if (entry.getWeeklySchedule() != null) {
                dto.setWeeklyScheduleId(entry.getWeeklySchedule().getId());
            }
        } catch (Exception e) {
            // Handle lazy loading exception
            dto.setWeeklyScheduleId(null);
        }

        try {
            if (entry.getStaff() != null) {
                dto.setStaffId(entry.getStaff().getId());
                dto.setStaffName(entry.getStaff().getFullName());
                dto.setStaffRole(entry.getStaff().getRole());

                // Safely access group which might be lazy-loaded
                try {
                    if (entry.getStaff().getGroup() != null) {
                        dto.setGroupName(entry.getStaff().getGroup().getName());
                    }
                } catch (Exception groupEx) {
                    // Group not loaded, leave as null
                    dto.setGroupName(null);
                }
            }
        } catch (Exception e) {
            // Handle staff lazy loading exception
            dto.setStaffId(null);
            dto.setStaffName("Unknown");
            dto.setStaffRole("Unknown");
        }

        dto.setDayOfWeek(entry.getDayOfWeek());
        dto.setWorkDate(entry.getWorkDate());
        dto.setStartTime(entry.getStartTime());
        dto.setEndTime(entry.getEndTime());
        dto.setStatus(entry.getStatus());
        dto.setWorkingHoursMinutes(entry.getWorkingHoursMinutes());
        dto.setBreakMinutes(entry.getBreakMinutes());
        dto.setNotes(entry.getNotes());
        dto.setWorkingHoursFormatted();
        dto.setBreakTimeFormatted();

        return dto;
    }
}
