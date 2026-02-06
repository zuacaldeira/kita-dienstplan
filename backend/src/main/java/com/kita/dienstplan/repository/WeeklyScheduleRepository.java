package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.WeeklySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for WeeklySchedule entity
 */
@Repository
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {

    /**
     * Find schedule by week number and year
     */
    Optional<WeeklySchedule> findByWeekNumberAndYear(Integer weekNumber, Integer year);

    /**
     * Find all schedules for a specific year
     */
    List<WeeklySchedule> findByYearOrderByWeekNumberDesc(Integer year);

    /**
     * Find schedules ordered by year and week (most recent first)
     */
    List<WeeklySchedule> findAllByOrderByYearDescWeekNumberDesc();

    /**
     * Check if a week already exists
     */
    boolean existsByWeekNumberAndYear(Integer weekNumber, Integer year);

    /**
     * Find schedules within a year range
     */
    @Query("SELECT ws FROM WeeklySchedule ws WHERE ws.year BETWEEN :startYear AND :endYear ORDER BY ws.year DESC, ws.weekNumber DESC")
    List<WeeklySchedule> findByYearRange(@Param("startYear") Integer startYear, @Param("endYear") Integer endYear);
}
