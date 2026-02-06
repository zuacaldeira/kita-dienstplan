package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.ScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ScheduleEntry entity
 */
@Repository
public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {

    /**
     * Find all entries for a specific week
     */
    @Query("SELECT se FROM ScheduleEntry se " +
           "JOIN FETCH se.staff s " +
           "JOIN FETCH se.weeklySchedule ws " +
           "WHERE ws.weekNumber = :weekNumber AND ws.year = :year " +
           "ORDER BY s.fullName, se.dayOfWeek")
    List<ScheduleEntry> findByWeekNumberAndYear(@Param("weekNumber") Integer weekNumber, 
                                                 @Param("year") Integer year);

    /**
     * Find entries for a specific staff member in a week
     */
    @Query("SELECT se FROM ScheduleEntry se " +
           "WHERE se.staff.id = :staffId " +
           "AND se.weeklySchedule.weekNumber = :weekNumber " +
           "AND se.weeklySchedule.year = :year " +
           "ORDER BY se.dayOfWeek")
    List<ScheduleEntry> findByStaffAndWeek(@Param("staffId") Long staffId,
                                           @Param("weekNumber") Integer weekNumber,
                                           @Param("year") Integer year);

    /**
     * Find entries for a specific date
     */
    List<ScheduleEntry> findByWorkDateOrderByStaff_FullName(LocalDate workDate);

    /**
     * Find who is working at a specific date and time
     */
    @Query("SELECT se FROM ScheduleEntry se " +
           "JOIN FETCH se.staff s " +
           "WHERE se.workDate = :date " +
           "AND se.status = 'normal' " +
           "AND se.startTime <= :time " +
           "AND se.endTime >= :time " +
           "ORDER BY s.fullName")
    List<ScheduleEntry> findWhoIsWorkingAt(@Param("date") LocalDate date, 
                                            @Param("time") LocalTime time);

    /**
     * Find entries by status (e.g., all sick days)
     */
    @Query("SELECT se FROM ScheduleEntry se " +
           "JOIN FETCH se.staff s " +
           "WHERE se.status = :status " +
           "AND se.workDate >= :startDate " +
           "AND se.workDate <= :endDate " +
           "ORDER BY se.workDate, s.fullName")
    List<ScheduleEntry> findByStatusAndDateRange(@Param("status") String status,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * Get daily totals (with and without Praktikanten)
     */
    @Query("SELECT " +
           "se.dayOfWeek, " +
           "se.workDate, " +
           "SUM(CASE WHEN s.isPraktikant = false THEN se.workingHoursMinutes ELSE 0 END), " +
           "SUM(se.workingHoursMinutes), " +
           "COUNT(CASE WHEN s.isPraktikant = false THEN 1 END), " +
           "COUNT(*) " +
           "FROM ScheduleEntry se " +
           "JOIN se.staff s " +
           "WHERE se.weeklySchedule.weekNumber = :weekNumber " +
           "AND se.weeklySchedule.year = :year " +
           "GROUP BY se.dayOfWeek, se.workDate " +
           "ORDER BY se.dayOfWeek")
    List<Object[]> getDailyTotals(@Param("weekNumber") Integer weekNumber, 
                                   @Param("year") Integer year);

    /**
     * Get weekly totals per staff member
     */
    @Query("SELECT " +
           "s.id, " +
           "s.fullName, " +
           "s.role, " +
           "g.name, " +
           "SUM(se.workingHoursMinutes), " +
           "SUM(se.breakMinutes), " +
           "SUM(CASE WHEN se.status = 'normal' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN se.status = 'krank' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN se.status = 'frei' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN se.status IN ('Schule', 'Fachschule') THEN 1 ELSE 0 END) " +
           "FROM ScheduleEntry se " +
           "JOIN se.staff s " +
           "LEFT JOIN s.group g " +
           "WHERE se.weeklySchedule.weekNumber = :weekNumber " +
           "AND se.weeklySchedule.year = :year " +
           "GROUP BY s.id, s.fullName, s.role, g.name " +
           "ORDER BY g.name, s.fullName")
    List<Object[]> getWeeklyStaffTotals(@Param("weekNumber") Integer weekNumber, 
                                        @Param("year") Integer year);

    /**
     * Check if entry already exists for staff on a specific day in a week
     */
    boolean existsByWeeklySchedule_IdAndStaff_IdAndDayOfWeek(Long weeklyScheduleId, 
                                                              Long staffId, 
                                                              Integer dayOfWeek);
}
