package com.kita.dienstplan.dto;

import com.kita.dienstplan.entity.Group;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Group entity.
 * Prevents circular reference issues by excluding staffMembers collection.
 */
public class GroupDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Integer staffCount;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public GroupDTO() {
    }

    /**
     * Convert Group entity to DTO.
     * Calculates staffCount from staffMembers collection size.
     */
    public static GroupDTO fromEntity(@NonNull Group entity) {
        GroupDTO dto = new GroupDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Calculate staff count from collection
        try {
            if (entity.getStaffMembers() != null) {
                dto.setStaffCount(entity.getStaffMembers().size());
            } else {
                dto.setStaffCount(0);
            }
        } catch (Exception e) {
            // Staff members not loaded, set to 0
            dto.setStaffCount(0);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getStaffCount() {
        return staffCount;
    }

    public void setStaffCount(Integer staffCount) {
        this.staffCount = staffCount;
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
