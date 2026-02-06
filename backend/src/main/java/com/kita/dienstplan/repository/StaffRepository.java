package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Staff entity
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * Find staff by full name
     */
    Optional<Staff> findByFullName(String fullName);

    /**
     * Find all active staff
     */
    List<Staff> findByIsActiveTrueOrderByFullName();

    /**
     * Find staff by group ID
     */
    @Query("SELECT s FROM Staff s WHERE s.group.id = :groupId AND s.isActive = true ORDER BY s.fullName")
    List<Staff> findByGroupIdAndActive(@Param("groupId") Long groupId);

    /**
     * Find staff by group name
     */
    @Query("SELECT s FROM Staff s WHERE s.group.name = :groupName AND s.isActive = true ORDER BY s.fullName")
    List<Staff> findByGroupNameAndActive(@Param("groupName") String groupName);

    /**
     * Find all Praktikanten (interns)
     */
    List<Staff> findByIsPraktikantTrueAndIsActiveTrueOrderByFullName();

    /**
     * Find staff by role
     */
    List<Staff> findByRoleAndIsActiveTrueOrderByFullName(String role);

    /**
     * Check if full name exists
     */
    boolean existsByFullName(String fullName);

    /**
     * Find staff with schedule entries for a specific week
     */
    @Query("SELECT DISTINCT s FROM Staff s " +
           "JOIN s.scheduleEntries se " +
           "WHERE se.weeklySchedule.weekNumber = :weekNumber " +
           "AND se.weeklySchedule.year = :year " +
           "ORDER BY s.fullName")
    List<Staff> findStaffWithScheduleForWeek(@Param("weekNumber") Integer weekNumber, 
                                             @Param("year") Integer year);
}
