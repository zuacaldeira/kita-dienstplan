package com.kita.dienstplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for WeeklyScheduleController
 * Tests REST API endpoints for weekly schedule management
 */
@WebMvcTest(WeeklyScheduleController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class WeeklyScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WeeklyScheduleRepository weeklyScheduleRepository;

    // Security components (needed for Spring Security to initialize)
    @MockBean
    private com.kita.dienstplan.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private WeeklySchedule testWeeklySchedule;

    @BeforeEach
    void setUp() {
        testWeeklySchedule = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        testWeeklySchedule.setId(1L);
    }

    @Test
    void getAllWeeklySchedules_ShouldReturn200OrderedByYearDescWeekNumberDesc() throws Exception {
        // Arrange
        WeeklySchedule week1_2026 = TestDataBuilder.createTestWeeklySchedule(1, 2026);
        week1_2026.setId(1L);

        WeeklySchedule week10_2026 = TestDataBuilder.createTestWeeklySchedule(10, 2026);
        week10_2026.setId(2L);

        WeeklySchedule week5_2025 = TestDataBuilder.createTestWeeklySchedule(5, 2025);
        week5_2025.setId(3L);

        // Repository returns ordered by year DESC, weekNumber DESC
        when(weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc())
                .thenReturn(Arrays.asList(week10_2026, week1_2026, week5_2025));

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].year", is(2026)))
                .andExpect(jsonPath("$[0].weekNumber", is(10)))
                .andExpect(jsonPath("$[1].year", is(2026)))
                .andExpect(jsonPath("$[1].weekNumber", is(1)))
                .andExpect(jsonPath("$[2].year", is(2025)))
                .andExpect(jsonPath("$[2].weekNumber", is(5)));

        verify(weeklyScheduleRepository, times(1)).findAllByOrderByYearDescWeekNumberDesc();
    }

    @Test
    void getAllWeeklySchedules_WithEmptyResults_ShouldReturn200EmptyArray() throws Exception {
        // Arrange
        when(weeklyScheduleRepository.findAllByOrderByYearDescWeekNumberDesc())
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getWeeklyScheduleById_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        when(weeklyScheduleRepository.findById(1L)).thenReturn(Optional.of(testWeeklySchedule));

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.weekNumber", is(5)))
                .andExpect(jsonPath("$.year", is(2026)))
                .andExpect(jsonPath("$.notes", is("Test week schedule")));

        verify(weeklyScheduleRepository, times(1)).findById(1L);
    }

    @Test
    void getWeeklyScheduleById_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        when(weeklyScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules/999"))
                .andExpect(status().isNotFound());

        verify(weeklyScheduleRepository, times(1)).findById(999L);
    }

    @Test
    void getWeeklyScheduleByWeek_WithValidWeek_ShouldReturn200() throws Exception {
        // Arrange
        when(weeklyScheduleRepository.findByWeekNumberAndYear(5, 2026))
                .thenReturn(Optional.of(testWeeklySchedule));

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules/week/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.weekNumber", is(5)))
                .andExpect(jsonPath("$.year", is(2026)));

        verify(weeklyScheduleRepository, times(1)).findByWeekNumberAndYear(5, 2026);
    }

    @Test
    void getWeeklyScheduleByWeek_WithNonExistentWeek_ShouldReturn404() throws Exception {
        // Arrange
        when(weeklyScheduleRepository.findByWeekNumberAndYear(99, 2099))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/weekly-schedules/week/2099/99"))
                .andExpect(status().isNotFound());

        verify(weeklyScheduleRepository, times(1)).findByWeekNumberAndYear(99, 2099);
    }

    @Test
    void createWeeklySchedule_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "weekNumber": 10,
                    "year": 2026,
                    "startDate": "2026-03-02",
                    "endDate": "2026-03-08",
                    "notes": "New week schedule"
                }
                """;

        WeeklySchedule newSchedule = TestDataBuilder.createTestWeeklySchedule(10, 2026);
        newSchedule.setId(10L);
        newSchedule.setNotes("New week schedule");

        when(weeklyScheduleRepository.save(any(WeeklySchedule.class))).thenReturn(newSchedule);

        // Act & Assert
        mockMvc.perform(post("/api/weekly-schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.weekNumber", is(10)))
                .andExpect(jsonPath("$.year", is(2026)))
                .andExpect(jsonPath("$.notes", is("New week schedule")));

        verify(weeklyScheduleRepository, times(1)).save(any(WeeklySchedule.class));
    }

    @Test
    void updateWeeklySchedule_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "weekNumber": 6,
                    "year": 2026,
                    "startDate": "2026-02-09",
                    "endDate": "2026-02-15",
                    "notes": "Updated notes"
                }
                """;

        WeeklySchedule updatedSchedule = TestDataBuilder.createTestWeeklySchedule(6, 2026);
        updatedSchedule.setId(1L);
        updatedSchedule.setNotes("Updated notes");

        when(weeklyScheduleRepository.findById(1L)).thenReturn(Optional.of(testWeeklySchedule));
        when(weeklyScheduleRepository.save(any(WeeklySchedule.class))).thenReturn(updatedSchedule);

        // Act & Assert
        mockMvc.perform(put("/api/weekly-schedules/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.weekNumber", is(6)))
                .andExpect(jsonPath("$.notes", is("Updated notes")));

        verify(weeklyScheduleRepository, times(1)).findById(1L);
        verify(weeklyScheduleRepository, times(1)).save(any(WeeklySchedule.class));
    }

    @Test
    void updateWeeklySchedule_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "weekNumber": 6,
                    "year": 2026,
                    "startDate": "2026-02-09",
                    "endDate": "2026-02-15",
                    "notes": "Updated"
                }
                """;

        when(weeklyScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/weekly-schedules/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(weeklyScheduleRepository, times(1)).findById(999L);
        verify(weeklyScheduleRepository, never()).save(any(WeeklySchedule.class));
    }

    @Test
    void updateWeeklySchedule_ShouldUpdateAllFiveFields() throws Exception {
        // Arrange - Test all 5 updatable fields
        String requestBody = """
                {
                    "weekNumber": 15,
                    "year": 2027,
                    "startDate": "2027-04-12",
                    "endDate": "2027-04-18",
                    "notes": "All fields updated"
                }
                """;

        when(weeklyScheduleRepository.findById(1L)).thenReturn(Optional.of(testWeeklySchedule));
        when(weeklyScheduleRepository.save(any(WeeklySchedule.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act & Assert
        mockMvc.perform(put("/api/weekly-schedules/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(weeklyScheduleRepository, times(1)).save(argThat(schedule ->
                schedule.getWeekNumber() == 15 &&
                schedule.getYear() == 2027 &&
                schedule.getStartDate().equals(LocalDate.of(2027, 4, 12)) &&
                schedule.getEndDate().equals(LocalDate.of(2027, 4, 18)) &&
                schedule.getNotes().equals("All fields updated")
        ));
    }

    @Test
    void deleteWeeklySchedule_WithValidId_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(weeklyScheduleRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/weekly-schedules/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(weeklyScheduleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteWeeklySchedule_ShouldNotCheckExistence() throws Exception {
        // Even with non-existent ID, deleteById doesn't throw
        doNothing().when(weeklyScheduleRepository).deleteById(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/weekly-schedules/999")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(weeklyScheduleRepository, times(1)).deleteById(999L);
    }

    @Test
    void createWeeklySchedule_WithMalformedJson_ShouldReturn400() throws Exception {
        // Arrange
        String malformedJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/weekly-schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(weeklyScheduleRepository, never()).save(any(WeeklySchedule.class));
    }
}
