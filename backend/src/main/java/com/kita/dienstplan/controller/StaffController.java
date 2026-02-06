package com.kita.dienstplan.controller;

import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.GroupRepository;
import com.kita.dienstplan.repository.StaffRepository;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
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

    private final StaffRepository staffRepository;

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        return ResponseEntity.ok(staffRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Staff>> getActiveStaff() {
        return ResponseEntity.ok(staffRepository.findByIsActiveTrueOrderByFullName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable Long id) {
        return staffRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Staff>> getStaffByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(staffRepository.findByGroupIdAndActive(groupId));
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Staff saved = staffRepository.save(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Long id, @RequestBody Staff staff) {
        return staffRepository.findById(id)
                .map(existing -> {
                    // Update fields
                    existing.setFirstName(staff.getFirstName());
                    existing.setLastName(staff.getLastName());
                    existing.setFullName(staff.getFullName());
                    existing.setRole(staff.getRole());
                    existing.setGroup(staff.getGroup());
                    existing.setEmploymentType(staff.getEmploymentType());
                    existing.setEmail(staff.getEmail());
                    existing.setPhone(staff.getPhone());
                    existing.setIsPraktikant(staff.getIsPraktikant());
                    existing.setIsActive(staff.getIsActive());
                    return ResponseEntity.ok(staffRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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

    private final GroupRepository groupRepository;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupRepository.findAllByOrderByName());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Group>> getActiveGroups() {
        return ResponseEntity.ok(groupRepository.findByIsActiveTrueOrderByName());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long id) {
        return groupRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group saved = groupRepository.save(group);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group group) {
        return groupRepository.findById(id)
                .map(existing -> {
                    existing.setName(group.getName());
                    existing.setDescription(group.getDescription());
                    existing.setIsActive(group.getIsActive());
                    return ResponseEntity.ok(groupRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<WeeklySchedule>> getAllWeeklySchedules() {
        return ResponseEntity.ok(weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WeeklySchedule> getWeeklyScheduleById(@PathVariable Long id) {
        return weeklyScheduleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/week/{year}/{weekNumber}")
    public ResponseEntity<WeeklySchedule> getWeeklyScheduleByWeek(
            @PathVariable Integer year,
            @PathVariable Integer weekNumber) {
        return weeklyScheduleRepository.findByWeekNumberAndYear(weekNumber, year)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WeeklySchedule> createWeeklySchedule(@RequestBody WeeklySchedule schedule) {
        WeeklySchedule saved = weeklyScheduleRepository.save(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WeeklySchedule> updateWeeklySchedule(
            @PathVariable Long id,
            @RequestBody WeeklySchedule schedule) {
        return weeklyScheduleRepository.findById(id)
                .map(existing -> {
                    existing.setWeekNumber(schedule.getWeekNumber());
                    existing.setYear(schedule.getYear());
                    existing.setStartDate(schedule.getStartDate());
                    existing.setEndDate(schedule.getEndDate());
                    existing.setNotes(schedule.getNotes());
                    return ResponseEntity.ok(weeklyScheduleRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeeklySchedule(@PathVariable Long id) {
        weeklyScheduleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
