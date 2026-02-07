package com.kita.dienstplan.service;

import com.kita.dienstplan.dto.StaffDTO;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.repository.GroupRepository;
import com.kita.dienstplan.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Staff entity operations.
 * All methods return DTOs to prevent circular reference issues.
 */
@Service
@Transactional(readOnly = true)
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private GroupRepository groupRepository;

    /**
     * Get all staff members.
     */
    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(StaffDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all active staff members.
     */
    public List<StaffDTO> getActiveStaff() {
        return staffRepository.findByIsActiveTrueOrderByFullName().stream()
                .map(StaffDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get staff by ID.
     */
    public Optional<StaffDTO> getStaffById(Long id) {
        return staffRepository.findById(id)
                .map(StaffDTO::fromEntity);
    }

    /**
     * Get staff members by group.
     */
    public List<StaffDTO> getStaffByGroup(Long groupId) {
        return staffRepository.findByGroupIdAndActive(groupId).stream()
                .map(StaffDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Create new staff member.
     */
    @Transactional
    public StaffDTO createStaff(Staff staff) {
        // Handle group assignment if provided
        if (staff.getGroup() != null && staff.getGroup().getId() != null) {
            Optional<Group> group = groupRepository.findById(staff.getGroup().getId());
            staff.setGroup(group.orElse(null));
        }

        Staff savedStaff = staffRepository.save(staff);
        return StaffDTO.fromEntity(savedStaff);
    }

    /**
     * Update existing staff member.
     */
    @Transactional
    public Optional<StaffDTO> updateStaff(Long id, Staff staffDetails) {
        return staffRepository.findById(id).map(staff -> {
            staff.setFirstName(staffDetails.getFirstName());
            staff.setLastName(staffDetails.getLastName());
            staff.setRole(staffDetails.getRole());
            staff.setEmploymentType(staffDetails.getEmploymentType());
            staff.setWeeklyHours(staffDetails.getWeeklyHours());
            staff.setEmail(staffDetails.getEmail());
            staff.setPhone(staffDetails.getPhone());
            staff.setIsPraktikant(staffDetails.getIsPraktikant());
            staff.setIsActive(staffDetails.getIsActive());
            staff.setHireDate(staffDetails.getHireDate());
            staff.setTerminationDate(staffDetails.getTerminationDate());

            // Handle group assignment
            if (staffDetails.getGroup() != null && staffDetails.getGroup().getId() != null) {
                Optional<Group> group = groupRepository.findById(staffDetails.getGroup().getId());
                staff.setGroup(group.orElse(null));
            } else {
                staff.setGroup(null);
            }

            Staff updatedStaff = staffRepository.save(staff);
            return StaffDTO.fromEntity(updatedStaff);
        });
    }

    /**
     * Delete staff member.
     */
    @Transactional
    public boolean deleteStaff(Long id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
