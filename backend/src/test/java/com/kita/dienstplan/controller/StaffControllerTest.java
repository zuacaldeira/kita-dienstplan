package com.kita.dienstplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.entity.Staff;
import com.kita.dienstplan.repository.StaffRepository;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StaffController
 * Tests REST API endpoints, request/response handling, and status codes
 */
@WebMvcTest(StaffController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffRepository staffRepository;

    // Security components (needed for Spring Security to initialize)
    @MockBean
    private com.kita.dienstplan.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private Staff testStaff;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = TestDataBuilder.createTestGroup();
        testGroup.setId(1L);

        testStaff = TestDataBuilder.createTestStaff("Max", "Mustermann", testGroup);
        testStaff.setId(1L);
    }

    @Test
    void getAllStaff_ShouldReturn200WithStaffList() throws Exception {
        // Arrange
        Staff staff2 = TestDataBuilder.createTestStaff("Anna", "Schmidt", testGroup);
        staff2.setId(2L);

        when(staffRepository.findAll()).thenReturn(Arrays.asList(testStaff, staff2));

        // Act & Assert
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].firstName", is("Max")))
                .andExpect(jsonPath("$[0].lastName", is("Mustermann")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].firstName", is("Anna")));

        verify(staffRepository, times(1)).findAll();
    }

    @Test
    void getAllStaff_WithEmptyResults_ShouldReturn200EmptyArray() throws Exception {
        // Arrange
        when(staffRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getActiveStaff_ShouldReturnOnlyActiveStaffOrderedByFullName() throws Exception {
        // Arrange
        Staff activeStaff1 = TestDataBuilder.createTestStaff("Zara", "Zimmermann", testGroup);
        activeStaff1.setId(2L);
        activeStaff1.setIsActive(true);

        Staff activeStaff2 = TestDataBuilder.createTestStaff("Anna", "Adams", testGroup);
        activeStaff2.setId(3L);
        activeStaff2.setIsActive(true);

        // Repository returns ordered by fullName
        when(staffRepository.findByIsActiveTrueOrderByFullName())
                .thenReturn(Arrays.asList(activeStaff2, testStaff, activeStaff1));

        // Act & Assert
        mockMvc.perform(get("/api/staff/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].fullName", is("Anna Adams")))
                .andExpect(jsonPath("$[1].fullName", is("Max Mustermann")))
                .andExpect(jsonPath("$[2].fullName", is("Zara Zimmermann")));

        verify(staffRepository, times(1)).findByIsActiveTrueOrderByFullName();
    }

    @Test
    void getStaffById_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));

        // Act & Assert
        mockMvc.perform(get("/api/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Max")))
                .andExpect(jsonPath("$.lastName", is("Mustermann")))
                .andExpect(jsonPath("$.fullName", is("Max Mustermann")))
                .andExpect(jsonPath("$.role", is("Erzieher")));

        verify(staffRepository, times(1)).findById(1L);
    }

    @Test
    void getStaffById_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/staff/999"))
                .andExpect(status().isNotFound());

        verify(staffRepository, times(1)).findById(999L);
    }

    @Test
    void getStaffByGroup_ShouldReturnStaffInGroup() throws Exception {
        // Arrange
        Staff staff2 = TestDataBuilder.createTestStaff("Lisa", "Meyer", testGroup);
        staff2.setId(2L);

        when(staffRepository.findByGroupIdAndActive(1L))
                .thenReturn(Arrays.asList(testStaff, staff2));

        // Act & Assert
        mockMvc.perform(get("/api/staff/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(staffRepository, times(1)).findByGroupIdAndActive(1L);
    }

    @Test
    void createStaff_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "firstName": "New",
                    "lastName": "Staff",
                    "fullName": "New Staff",
                    "role": "Erzieher",
                    "employmentType": "full-time",
                    "weeklyHours": 40.00,
                    "email": "new.staff@kita.de",
                    "phone": "+49 123 456789",
                    "isPraktikant": false,
                    "isActive": true
                }
                """;

        Staff newStaff = TestDataBuilder.createTestStaff("New", "Staff", null);
        newStaff.setId(10L);

        when(staffRepository.save(any(Staff.class))).thenReturn(newStaff);

        // Act & Assert
        mockMvc.perform(post("/api/staff")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.lastName", is("Staff")));

        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    void updateStaff_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "fullName": "Updated Name",
                    "role": "Leitung",
                    "employmentType": "part-time",
                    "weeklyHours": 20.00,
                    "email": "updated@kita.de",
                    "phone": "+49 987 654321",
                    "isPraktikant": false,
                    "isActive": true
                }
                """;

        Staff updatedStaff = TestDataBuilder.createTestStaff("Updated", "Name", testGroup);
        updatedStaff.setId(1L);
        updatedStaff.setRole("Leitung");
        updatedStaff.setEmploymentType("part-time");
        updatedStaff.setWeeklyHours(new BigDecimal("20.00"));

        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenReturn(updatedStaff);

        // Act & Assert
        mockMvc.perform(put("/api/staff/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.role", is("Leitung")));

        verify(staffRepository, times(1)).findById(1L);
        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    void updateStaff_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "firstName": "Updated",
                    "lastName": "Name",
                    "fullName": "Updated Name",
                    "role": "Erzieher"
                }
                """;

        when(staffRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/staff/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(staffRepository, times(1)).findById(999L);
        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void updateStaff_ShouldUpdateAllFields() throws Exception {
        // Arrange - Test all 8 updatable fields
        String requestBody = """
                {
                    "firstName": "NewFirst",
                    "lastName": "NewLast",
                    "fullName": "NewFirst NewLast",
                    "role": "Praktikant",
                    "employmentType": "intern",
                    "email": "new@email.com",
                    "phone": "+49 111 222333",
                    "isPraktikant": true,
                    "isActive": false
                }
                """;

        when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
        when(staffRepository.save(any(Staff.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act & Assert
        mockMvc.perform(put("/api/staff/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(staffRepository, times(1)).save(argThat(staff ->
                staff.getFirstName().equals("NewFirst") &&
                staff.getLastName().equals("NewLast") &&
                staff.getFullName().equals("NewFirst NewLast") &&
                staff.getRole().equals("Praktikant") &&
                staff.getEmail().equals("new@email.com") &&
                staff.getPhone().equals("+49 111 222333") &&
                staff.getIsPraktikant() == true &&
                staff.getIsActive() == false
        ));
    }

    @Test
    void deleteStaff_WithValidId_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(staffRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/staff/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(staffRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStaff_ShouldNotCheckExistence() throws Exception {
        // Even with non-existent ID, deleteById doesn't throw
        doNothing().when(staffRepository).deleteById(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/staff/999")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(staffRepository, times(1)).deleteById(999L);
    }

    @Test
    void createStaff_WithMalformedJson_ShouldReturn400() throws Exception {
        // Arrange
        String malformedJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/staff")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(staffRepository, never()).save(any(Staff.class));
    }

    @Test
    void getStaffByGroup_WithEmptyResults_ShouldReturn200EmptyArray() throws Exception {
        // Arrange
        when(staffRepository.findByGroupIdAndActive(999L))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/staff/group/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
