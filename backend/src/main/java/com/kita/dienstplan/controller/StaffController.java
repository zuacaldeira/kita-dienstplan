package com.kita.dienstplan.controller;

import com.kita.dienstplan.dto.GroupDTO;
import com.kita.dienstplan.dto.StaffDTO;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import com.kita.dienstplan.service.GroupService;
import com.kita.dienstplan.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Staff operations
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffDTO>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @GetMapping("/active")
    public ResponseEntity<List<StaffDTO>> getActiveStaff() {
        return ResponseEntity.ok(staffService.getActiveStaff());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDTO> getStaffById(@PathVariable Long id) {
        return staffService.getStaffById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StaffDTO>> getStaffByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(staffService.getStaffByGroup(groupId));
    }

    @PostMapping
    public ResponseEntity<StaffDTO> createStaff(@RequestBody Staff staff) {
        StaffDTO saved = staffService.createStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffDTO> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        return staffService.updateStaff(id, staff)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        if (staffService.deleteStaff(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

/**
 * REST Controller for Age Group operations
 */
@RestController
@RequestMapping("/api/age-groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GroupDTO>> getActiveGroups() {
        return ResponseEntity.ok(groupService.getActiveGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroupById(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@RequestBody Group group) {
        GroupDTO saved = groupService.createGroup(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        return groupService.updateGroup(id, group)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        if (groupService.deleteGroup(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

/**
 * REST Controller for WeeklySchedule operations
 */
@RestController
@RequestMapping("/api/weekly-schedules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class WeeklyScheduleController {

    private final WeeklyScheduleRepository weeklyScheduleRepository;

    @GetMapping
    public ResponseEntity<List<com.kita.dienstplan.dto.WeeklyScheduleDTO>> getAllWeeklySchedules() {
        List<WeeklySchedule> schedules = weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc();
        List<com.kita.dienstplan.dto.WeeklyScheduleDTO> dtos = schedules.stream()
                .map(com.kita.dienstplan.dto.WeeklyScheduleDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.kita.dienstplan.dto.WeeklyScheduleDTO> getWeeklyScheduleById(@PathVariable Long id) {
        return weeklyScheduleRepository.findById(id)
                .map(com.kita.dienstplan.dto.WeeklyScheduleDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/week/{year}/{weekNumber}")
    public ResponseEntity<com.kita.dienstplan.dto.WeeklyScheduleDTO> getWeeklyScheduleByWeek(
            @PathVariable Integer year,
            @PathVariable Integer weekNumber) {
        return weeklyScheduleRepository.findByWeekNumberAndYear(weekNumber, year)
                .map(com.kita.dienstplan.dto.WeeklyScheduleDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<com.kita.dienstplan.dto.WeeklyScheduleDTO> createWeeklySchedule(@RequestBody WeeklySchedule schedule) {
        WeeklySchedule saved = weeklyScheduleRepository.save(schedule);
        com.kita.dienstplan.dto.WeeklyScheduleDTO dto = com.kita.dienstplan.dto.WeeklyScheduleDTO.fromEntity(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.kita.dienstplan.dto.WeeklyScheduleDTO> updateWeeklySchedule(
            @PathVariable Long id,
            @RequestBody WeeklySchedule schedule) {
        return weeklyScheduleRepository.findById(id)
                .map(existing -> {
                    existing.setWeekNumber(schedule.getWeekNumber());
                    existing.setYear(schedule.getYear());
                    existing.setStartDate(schedule.getStartDate());
                    existing.setEndDate(schedule.getEndDate());
                    existing.setNotes(schedule.getNotes());
                    WeeklySchedule updated = weeklyScheduleRepository.save(existing);
                    com.kita.dienstplan.dto.WeeklyScheduleDTO dto = com.kita.dienstplan.dto.WeeklyScheduleDTO.fromEntity(updated);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeeklySchedule(@PathVariable Long id) {
        weeklyScheduleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
