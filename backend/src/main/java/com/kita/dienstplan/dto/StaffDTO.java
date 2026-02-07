package com.kita.dienstplan.dto;

import com.kita.dienstplan.entity.Staff;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Staff entity.
 * Prevents circular reference issues by using groupId/groupName instead of nested Group object.
 */
public class StaffDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String role;
    private Long groupId;
    private String groupName;
    private String employmentType;
    private BigDecimal weeklyHours;
    private String email;
    private String phone;
    private Boolean isPraktikant;
    private Boolean isActive;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public StaffDTO() {
    }

    /**
     * Convert Staff entity to DTO.
     * Safely handles lazy-loaded group relationship.
     */
    public static StaffDTO fromEntity(@NonNull Staff entity) {
        StaffDTO dto = new StaffDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setFullName(entity.getFullName());
        dto.setRole(entity.getRole());
        dto.setEmploymentType(entity.getEmploymentType());
        dto.setWeeklyHours(entity.getWeeklyHours());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setIsPraktikant(entity.getIsPraktikant());
        dto.setIsActive(entity.getIsActive());
        dto.setHireDate(entity.getHireDate());
        dto.setTerminationDate(entity.getTerminationDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Safely handle lazy-loaded group relationship
        try {
            if (entity.getGroup() != null) {
                dto.setGroupId(entity.getGroup().getId());
                dto.setGroupName(entity.getGroup().getName());
            }
        } catch (Exception e) {
            // Group not loaded, leave fields null
            dto.setGroupId(null);
            dto.setGroupName(null);
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public BigDecimal getWeeklyHours() {
        return weeklyHours;
    }

    public void setWeeklyHours(BigDecimal weeklyHours) {
        this.weeklyHours = weeklyHours;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsPraktikant() {
        return isPraktikant;
    }

    public void setIsPraktikant(Boolean isPraktikant) {
        this.isPraktikant = isPraktikant;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
