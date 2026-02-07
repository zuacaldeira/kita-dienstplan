package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.util.TestDataBuilder;
import com.kita.dienstplan.util.TestJpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for WeeklyScheduleRepository custom query methods
 * Tests edge cases like week 1, week 52/53, and year boundaries
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
@Sql(scripts = "/test-schema.sql")
class WeeklyScheduleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    private WeeklySchedule schedule2026Week5;
    private WeeklySchedule schedule2026Week10;
    private WeeklySchedule schedule2025Week52;

    @BeforeEach
    void setUp() {
        // Create test schedules for different weeks and years
        schedule2026Week5 = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        schedule2026Week10 = TestDataBuilder.createTestWeeklySchedule(10, 2026);
        schedule2025Week52 = TestDataBuilder.createTestWeeklySchedule(52, 2025);

        entityManager.persistAndFlush(schedule2026Week5);
        entityManager.persistAndFlush(schedule2026Week10);
        entityManager.persistAndFlush(schedule2025Week52);
        entityManager.clear();
    }

    @Test
    void findByWeekNumberAndYear_ExistingSchedule_ShouldReturnSchedule() {
        // Act
        Optional<WeeklySchedule> result = weeklyScheduleRepository.findByWeekNumberAndYear(5, 2026);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getWeekNumber()).isEqualTo(5);
        assertThat(result.get().getYear()).isEqualTo(2026);
    }

    @Test
    void findByWeekNumberAndYear_NonExistingSchedule_ShouldReturnEmpty() {
        // Act
        Optional<WeeklySchedule> result = weeklyScheduleRepository.findByWeekNumberAndYear(99, 2099);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByWeekNumberAndYear_Week1_ShouldWork() {
        // Arrange - Create week 1
        WeeklySchedule week1 = TestDataBuilder.createTestWeeklySchedule(1, 2026);
        entityManager.persistAndFlush(week1);

        // Act
        Optional<WeeklySchedule> result = weeklyScheduleRepository.findByWeekNumberAndYear(1, 2026);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getWeekNumber()).isEqualTo(1);
    }

    @Test
    void findByWeekNumberAndYear_Week53_ShouldWork() {
        // Arrange - Create week 53 (some years have 53 weeks)
        WeeklySchedule week53 = TestDataBuilder.createTestWeeklySchedule(53, 2020);
        entityManager.persistAndFlush(week53);

        // Act
        Optional<WeeklySchedule> result = weeklyScheduleRepository.findByWeekNumberAndYear(53, 2020);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getWeekNumber()).isEqualTo(53);
    }

    @Test
    void findByYearOrderByWeekNumberDesc_ShouldReturnSchedulesForYearDescending() {
        // Arrange - Create more schedules for 2026
        WeeklySchedule schedule2026Week1 = TestDataBuilder.createTestWeeklySchedule(1, 2026);
        entityManager.persistAndFlush(schedule2026Week1);

        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearOrderByWeekNumberDesc(2026);

        // Assert
        assertThat(result).hasSize(3); // Week 1, 5, 10
        assertThat(result)
            .extracting(WeeklySchedule::getWeekNumber)
            .containsExactly(10, 5, 1); // Descending order
    }

    @Test
    void findByYearOrderByWeekNumberDesc_NonExistingYear_ShouldReturnEmptyList() {
        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearOrderByWeekNumberDesc(2099);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByOrderByYearDescWeekNumberDesc_ShouldReturnAllSchedulesSorted() {
        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc();

        // Assert
        assertThat(result).hasSize(3);
        // Should be: 2026 week 10, 2026 week 5, 2025 week 52
        assertThat(result.get(0).getYear()).isEqualTo(2026);
        assertThat(result.get(0).getWeekNumber()).isEqualTo(10);
        assertThat(result.get(1).getYear()).isEqualTo(2026);
        assertThat(result.get(1).getWeekNumber()).isEqualTo(5);
        assertThat(result.get(2).getYear()).isEqualTo(2025);
        assertThat(result.get(2).getWeekNumber()).isEqualTo(52);
    }

    @Test
    void findAllByOrderByYearDescWeekNumberDesc_EmptyDatabase_ShouldReturnEmptyList() {
        // Arrange - Clear database
        weeklyScheduleRepository.deleteAll();
        entityManager.flush();

        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByWeekNumberAndYear_ExistingSchedule_ShouldReturnTrue() {
        // Act
        boolean exists = weeklyScheduleRepository.existsByWeekNumberAndYear(5, 2026);

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByWeekNumberAndYear_NonExistingSchedule_ShouldReturnFalse() {
        // Act
        boolean exists = weeklyScheduleRepository.existsByWeekNumberAndYear(99, 2099);

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void findByYearRange_WithinRange_ShouldReturnSchedules() {
        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearRange(2025, 2026);

        // Assert
        assertThat(result).hasSize(3); // All 3 schedules are in 2025-2026
        assertThat(result)
            .extracting(WeeklySchedule::getYear)
            .containsOnly(2025, 2026);
    }

    @Test
    void findByYearRange_SingleYear_ShouldReturnSchedulesForThatYear() {
        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearRange(2026, 2026);

        // Assert
        assertThat(result).hasSize(2); // Only 2026 schedules
        assertThat(result).allMatch(ws -> ws.getYear().equals(2026));
        assertThat(result)
            .extracting(WeeklySchedule::getWeekNumber)
            .containsExactly(10, 5); // Descending order
    }

    @Test
    void findByYearRange_OutsideRange_ShouldReturnEmptyList() {
        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearRange(2030, 2035);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByYearRange_YearBoundary_ShouldIncludeBothYears() {
        // Arrange - Create schedule for 2024
        WeeklySchedule schedule2024 = TestDataBuilder.createTestWeeklySchedule(1, 2024);
        entityManager.persistAndFlush(schedule2024);

        // Act
        List<WeeklySchedule> result = weeklyScheduleRepository.findByYearRange(2024, 2025);

        // Assert
        assertThat(result).hasSize(2); // 2024 and 2025 schedules
        assertThat(result)
            .extracting(WeeklySchedule::getYear)
            .containsExactlyInAnyOrder(2024, 2025);
    }
}
