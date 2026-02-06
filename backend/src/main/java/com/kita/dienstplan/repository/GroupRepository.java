package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Group entity
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Find group by name
     */
    Optional<Group> findByName(String name);

    /**
     * Find all active groups
     */
    List<Group> findByIsActiveTrueOrderByName();

    /**
     * Find all groups (active and inactive)
     */
    List<Group> findAllByOrderByName();

    /**
     * Check if group name exists
     */
    boolean existsByName(String name);
}
