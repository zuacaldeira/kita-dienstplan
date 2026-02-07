package com.kita.dienstplan.service;

import com.kita.dienstplan.dto.GroupDTO;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Group entity operations.
 * All methods return DTOs to prevent circular reference issues.
 */
@Service
@Transactional(readOnly = true)
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    /**
     * Get all groups.
     */
    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all active groups.
     */
    public List<GroupDTO> getActiveGroups() {
        return groupRepository.findByIsActiveTrueOrderByName().stream()
                .map(GroupDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get group by ID.
     */
    public Optional<GroupDTO> getGroupById(Long id) {
        return groupRepository.findById(id)
                .map(GroupDTO::fromEntity);
    }

    /**
     * Create new group.
     */
    @Transactional
    public GroupDTO createGroup(Group group) {
        Group savedGroup = groupRepository.save(group);
        return GroupDTO.fromEntity(savedGroup);
    }

    /**
     * Update existing group.
     */
    @Transactional
    public Optional<GroupDTO> updateGroup(Long id, Group groupDetails) {
        return groupRepository.findById(id).map(group -> {
            group.setName(groupDetails.getName());
            group.setDescription(groupDetails.getDescription());
            group.setIsActive(groupDetails.getIsActive());

            Group updatedGroup = groupRepository.save(group);
            return GroupDTO.fromEntity(updatedGroup);
        });
    }

    /**
     * Delete group.
     */
    @Transactional
    public boolean deleteGroup(Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
