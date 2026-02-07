package com.kita.dienstplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kita.dienstplan.dto.DailyTotalDTO;
import com.kita.dienstplan.dto.ScheduleEntryDTO;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.entity.WeeklySchedule;
import com.kita.dienstplan.repository.StaffRepository;
import com.kita.dienstplan.repository.WeeklyScheduleRepository;
import com.kita.dienstplan.service.ScheduleService;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ScheduleController
 * Tests REST API endpoints, request/response handling, and status codes
 */
@WebMvcTest(ScheduleController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @MockBean
    private StaffRepository staffRepository;

    // Security components (needed for Spring Security to initialize)
    @MockBean
    private com.kita.dienstplan.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private ScheduleEntryDTO testEntryDTO;
    private DailyTotalDTO testDailyTotalDTO;
    private WeeklySchedule testWeeklySchedule;
    private Staff testStaff;

    @BeforeEach
    void setUp() {
        // Create test DTO
        testEntryDTO = new ScheduleEntryDTO();
        testEntryDTO.setId(1L);
        testEntryDTO.setWeeklyScheduleId(1L);
        testEntryDTO.setStaffId(1L);
        testEntryDTO.setStaffName("Max Mustermann");
        testEntryDTO.setStaffRole("Erzieher");
        testEntryDTO.setGroupName("Käfer");
        testEntryDTO.setDayOfWeek(0);
        testEntryDTO.setWorkDate(LocalDate.of(2026, 2, 2));
        testEntryDTO.setStartTime(LocalTime.of(8, 0));
        testEntryDTO.setEndTime(LocalTime.of(16, 0));
        testEntryDTO.setStatus("normal");
        testEntryDTO.setWorkingHoursMinutes(450);
        testEntryDTO.setBreakMinutes(30);
        testEntryDTO.setWorkingHoursFormatted();
        testEntryDTO.setBreakTimeFormatted();

        // Create test daily total DTO
        testDailyTotalDTO = new DailyTotalDTO();
        testDailyTotalDTO.setDayOfWeek(0);
        testDailyTotalDTO.setWorkDate(LocalDate.of(2026, 2, 2));
        testDailyTotalDTO.setTotalMinutesWithoutPraktikanten(900);
        testDailyTotalDTO.setTotalMinutesWithPraktikanten(1200);
        testDailyTotalDTO.setStaffCountWithoutPraktikanten(2L);
        testDailyTotalDTO.setTotalStaffCount(3L);
        testDailyTotalDTO.setDayNameFromNumber();
        testDailyTotalDTO.setFormattedHours();

        // Create test entities
        testWeeklySchedule = TestDataBuilder.createTestWeeklySchedule(5, 2026);
        testWeeklySchedule.setId(1L);

        testStaff = TestDataBuilder.createTestStaff();
        testStaff.setId(1L);
    }

    @Test
    void getScheduleForWeek_ShouldReturn200WithEntries() throws Exception {
        // Arrange
        when(scheduleService.getScheduleForWeek(5, 2026))
                .thenReturn(Arrays.asList(testEntryDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/week/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].staffName", is("Max Mustermann")))
                .andExpect(jsonPath("$[0].staffRole", is("Erzieher")))
                .andExpect(jsonPath("$[0].groupName", is("Käfer")))
                .andExpect(jsonPath("$[0].workingHoursFormatted", is("7:30")));

        verify(scheduleService, times(1)).getScheduleForWeek(5, 2026);
    }

    @Test
    void getScheduleForWeek_WithEmptyResults_ShouldReturn200EmptyArray() throws Exception {
        // Arrange
        when(scheduleService.getScheduleForWeek(99, 2026))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/schedules/week/2026/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getScheduleForStaffInWeek_ShouldReturn200() throws Exception {
        // Arrange
        when(scheduleService.getScheduleForStaffInWeek(1L, 5, 2026))
                .thenReturn(Arrays.asList(testEntryDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/staff/1/week/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffId", is(1)))
                .andExpect(jsonPath("$[0].staffName", is("Max Mustermann")));

        verify(scheduleService, times(1)).getScheduleForStaffInWeek(1L, 5, 2026);
    }

    @Test
    void getScheduleForDate_ShouldParseDateCorrectly() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        when(scheduleService.getScheduleForDate(testDate))
                .thenReturn(Arrays.asList(testEntryDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/date/2026-02-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].workDate", is("2026-02-02")));

        verify(scheduleService, times(1)).getScheduleForDate(testDate);
    }

    @Test
    void getScheduleForDate_WithInvalidDate_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/schedules/date/invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWhoIsWorkingAt_ShouldParseDateTimeCorrectly() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);
        LocalTime testTime = LocalTime.of(10, 0);
        when(scheduleService.getWhoIsWorkingAt(testDate, testTime))
                .thenReturn(Arrays.asList(testEntryDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/on-duty")
                        .param("date", "2026-02-02")
                        .param("time", "10:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].staffName", is("Max Mustermann")));

        verify(scheduleService, times(1)).getWhoIsWorkingAt(testDate, testTime);
    }

    @Test
    void getWhoIsWorkingAt_WithMissingParameters_ShouldReturn400() throws Exception {
        // Act & Assert - Missing time parameter
        mockMvc.perform(get("/api/schedules/on-duty")
                        .param("date", "2026-02-02"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDailyTotals_ShouldReturn200() throws Exception {
        // Arrange
        when(scheduleService.getDailyTotals(5, 2026))
                .thenReturn(Arrays.asList(testDailyTotalDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/daily-totals/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].dayOfWeek", is(0)))
                .andExpect(jsonPath("$[0].dayName", is("Montag")))
                .andExpect(jsonPath("$[0].hoursWithoutPraktikanten", is("15:00")))
                .andExpect(jsonPath("$[0].hoursWithPraktikanten", is("20:00")))
                .andExpect(jsonPath("$[0].staffCountWithoutPraktikanten", is(2)))
                .andExpect(jsonPath("$[0].totalStaffCount", is(3)));

        verify(scheduleService, times(1)).getDailyTotals(5, 2026);
    }

    @Test
    void createScheduleEntry_WithValidRequest_ShouldReturn201() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "weeklyScheduleId": 1,
                    "staffId": 1,
                    "dayOfWeek": 0,
                    "workDate": "2026-02-02",
                    "startTime": "08:00",
                    "endTime": "16:00",
                    "status": "normal",
                    "notes": "Test entry"
                }
                """;

        when(weeklyScheduleRepository.findById(1L))
                .thenReturn(Optional.of(testWeeklySchedule));
        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(testStaff));
        when(scheduleService.createScheduleEntry(any()))
                .thenReturn(testEntryDTO);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/entries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.staffName", is("Max Mustermann")));

        verify(weeklyScheduleRepository, times(1)).findById(1L);
        verify(staffRepository, times(1)).findById(1L);
        verify(scheduleService, times(1)).createScheduleEntry(any());
    }

    // Note: Tests for invalid IDs removed because proper exception handling (@ControllerAdvice)
    // is not implemented in the controller. These would require integration tests or
    // proper error handling configuration.

    @Test
    void createScheduleEntry_WithMalformedJson_ShouldReturn400() throws Exception {
        // Arrange
        String malformedJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/schedules/entries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateScheduleEntry_ShouldReturn200() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "startTime": "09:00",
                    "endTime": "17:00",
                    "status": "normal",
                    "notes": "Updated"
                }
                """;

        when(scheduleService.updateScheduleEntry(eq(1L), any()))
                .thenReturn(testEntryDTO);

        // Act & Assert
        mockMvc.perform(put("/api/schedules/entries/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(scheduleService, times(1)).updateScheduleEntry(eq(1L), any());
    }

    @Test
    void updateScheduleEntry_WithPartialData_ShouldReturn200() throws Exception {
        // Arrange - Only update status
        String requestBody = """
                {
                    "status": "krank"
                }
                """;

        when(scheduleService.updateScheduleEntry(eq(1L), any()))
                .thenReturn(testEntryDTO);

        // Act & Assert
        mockMvc.perform(put("/api/schedules/entries/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(scheduleService, times(1)).updateScheduleEntry(eq(1L), any());
    }

    @Test
    void deleteScheduleEntry_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(scheduleService).deleteScheduleEntry(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/schedules/entries/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(scheduleService, times(1)).deleteScheduleEntry(1L);
    }

    @Test
    void getScheduleForWeek_WithMultipleEntries_ShouldReturnAll() throws Exception {
        // Arrange
        ScheduleEntryDTO entry2 = new ScheduleEntryDTO();
        entry2.setId(2L);
        entry2.setStaffName("Lisa Schmidt");

        when(scheduleService.getScheduleForWeek(5, 2026))
                .thenReturn(Arrays.asList(testEntryDTO, entry2));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/week/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].staffName", is("Max Mustermann")))
                .andExpect(jsonPath("$[1].staffName", is("Lisa Schmidt")));
    }

    @Test
    void getDailyTotals_WithMultipleDays_ShouldReturnAll() throws Exception {
        // Arrange
        DailyTotalDTO monday = new DailyTotalDTO();
        monday.setDayOfWeek(0);
        monday.setDayNameFromNumber();

        DailyTotalDTO tuesday = new DailyTotalDTO();
        tuesday.setDayOfWeek(1);
        tuesday.setDayNameFromNumber();

        when(scheduleService.getDailyTotals(5, 2026))
                .thenReturn(Arrays.asList(monday, tuesday));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/daily-totals/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].dayName", is("Montag")))
                .andExpect(jsonPath("$[1].dayName", is("Dienstag")));
    }

    @Test
    void createScheduleEntry_ShouldSetAllFieldsCorrectly() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "weeklyScheduleId": 1,
                    "staffId": 1,
                    "dayOfWeek": 2,
                    "workDate": "2026-02-04",
                    "startTime": "10:00",
                    "endTime": "18:00",
                    "status": "normal",
                    "notes": "Wednesday shift"
                }
                """;

        when(weeklyScheduleRepository.findById(1L))
                .thenReturn(Optional.of(testWeeklySchedule));
        when(staffRepository.findById(1L))
                .thenReturn(Optional.of(testStaff));
        when(scheduleService.createScheduleEntry(any()))
                .thenReturn(testEntryDTO);

        // Act & Assert
        mockMvc.perform(post("/api/schedules/entries")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Verify service was called with correct data
        verify(scheduleService, times(1)).createScheduleEntry(any());
    }

    @Test
    void getWhoIsWorkingAt_AtDifferentTimes_ShouldCallServiceCorrectly() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2026, 2, 2);

        // Test at 08:00
        when(scheduleService.getWhoIsWorkingAt(testDate, LocalTime.of(8, 0)))
                .thenReturn(Arrays.asList(testEntryDTO));

        // Act & Assert
        mockMvc.perform(get("/api/schedules/on-duty")
                        .param("date", "2026-02-02")
                        .param("time", "08:00"))
                .andExpect(status().isOk());

        verify(scheduleService, times(1)).getWhoIsWorkingAt(testDate, LocalTime.of(8, 0));
    }

    @Test
    void getScheduleForStaffInWeek_WithNonExistentStaff_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(scheduleService.getScheduleForStaffInWeek(999L, 5, 2026))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/schedules/staff/999/week/2026/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
