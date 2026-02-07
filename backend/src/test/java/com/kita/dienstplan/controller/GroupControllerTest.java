package com.kita.dienstplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kita.dienstplan.entity.Group;
import com.kita.dienstplan.repository.GroupRepository;
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
 * Integration tests for GroupController
 * Tests REST API endpoints for age group management
 */
@WebMvcTest(GroupController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupRepository groupRepository;

    // Security components (needed for Spring Security to initialize)
    @MockBean
    private com.kita.dienstplan.security.JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    private Group testGroup;

    @BeforeEach
    void setUp() {
        testGroup = TestDataBuilder.createTestGroup("Käfer", "Die Käfergruppe");
        testGroup.setId(1L);
    }

    @Test
    void getAllGroups_ShouldReturn200WithGroupsOrderedByName() throws Exception {
        // Arrange
        Group group1 = TestDataBuilder.createTestGroup("Schmetterlinge", "Butterfly group");
        group1.setId(2L);

        Group group2 = TestDataBuilder.createTestGroup("Bienen", "Bee group");
        group2.setId(3L);

        // Repository returns ordered by name
        when(groupRepository.findAllByOrderByName())
                .thenReturn(Arrays.asList(group2, testGroup, group1));

        // Act & Assert
        mockMvc.perform(get("/api/age-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Bienen")))
                .andExpect(jsonPath("$[1].name", is("Käfer")))
                .andExpect(jsonPath("$[2].name", is("Schmetterlinge")));

        verify(groupRepository, times(1)).findAllByOrderByName();
    }

    @Test
    void getAllGroups_WithEmptyResults_ShouldReturn200EmptyArray() throws Exception {
        // Arrange
        when(groupRepository.findAllByOrderByName()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/age-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getActiveGroups_ShouldReturnOnlyActiveGroupsOrderedByName() throws Exception {
        // Arrange
        Group activeGroup1 = TestDataBuilder.createTestGroup("Zebras", "Active group");
        activeGroup1.setId(2L);
        activeGroup1.setIsActive(true);

        Group activeGroup2 = TestDataBuilder.createTestGroup("Ameisen", "Active group");
        activeGroup2.setId(3L);
        activeGroup2.setIsActive(true);

        // Repository returns ordered by name
        when(groupRepository.findByIsActiveTrueOrderByName())
                .thenReturn(Arrays.asList(activeGroup2, testGroup, activeGroup1));

        // Act & Assert
        mockMvc.perform(get("/api/age-groups/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Ameisen")))
                .andExpect(jsonPath("$[1].name", is("Käfer")))
                .andExpect(jsonPath("$[2].name", is("Zebras")));

        verify(groupRepository, times(1)).findByIsActiveTrueOrderByName();
    }

    @Test
    void getGroupById_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        // Act & Assert
        mockMvc.perform(get("/api/age-groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Käfer")))
                .andExpect(jsonPath("$.description", is("Die Käfergruppe")))
                .andExpect(jsonPath("$.isActive", is(true)));

        verify(groupRepository, times(1)).findById(1L);
    }

    @Test
    void getGroupById_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/age-groups/999"))
                .andExpect(status().isNotFound());

        verify(groupRepository, times(1)).findById(999L);
    }

    @Test
    void createGroup_WithValidData_ShouldReturn201() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": "Marienkäfer",
                    "description": "Ladybug group",
                    "isActive": true
                }
                """;

        Group newGroup = TestDataBuilder.createTestGroup("Marienkäfer", "Ladybug group");
        newGroup.setId(10L);

        when(groupRepository.save(any(Group.class))).thenReturn(newGroup);

        // Act & Assert
        mockMvc.perform(post("/api/age-groups")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Marienkäfer")))
                .andExpect(jsonPath("$.description", is("Ladybug group")));

        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    void updateGroup_WithValidId_ShouldReturn200() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": "Updated Group",
                    "description": "Updated description",
                    "isActive": false
                }
                """;

        Group updatedGroup = TestDataBuilder.createTestGroup("Updated Group", "Updated description");
        updatedGroup.setId(1L);
        updatedGroup.setIsActive(false);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);

        // Act & Assert
        mockMvc.perform(put("/api/age-groups/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Group")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.isActive", is(false)));

        verify(groupRepository, times(1)).findById(1L);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    void updateGroup_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "name": "Updated Group",
                    "description": "Updated description",
                    "isActive": true
                }
                """;

        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/age-groups/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());

        verify(groupRepository, times(1)).findById(999L);
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void updateGroup_ShouldUpdateAllThreeFields() throws Exception {
        // Arrange - Test all 3 updatable fields
        String requestBody = """
                {
                    "name": "New Name",
                    "description": "New Description",
                    "isActive": false
                }
                """;

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));
        when(groupRepository.save(any(Group.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act & Assert
        mockMvc.perform(put("/api/age-groups/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(groupRepository, times(1)).save(argThat(group ->
                group.getName().equals("New Name") &&
                group.getDescription().equals("New Description") &&
                group.getIsActive() == false
        ));
    }

    @Test
    void deleteGroup_WithValidId_ShouldReturn204() throws Exception {
        // Arrange
        doNothing().when(groupRepository).deleteById(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/age-groups/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(groupRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteGroup_ShouldNotCheckExistence() throws Exception {
        // Even with non-existent ID, deleteById doesn't throw
        doNothing().when(groupRepository).deleteById(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/age-groups/999")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(groupRepository, times(1)).deleteById(999L);
    }

    @Test
    void createGroup_WithMalformedJson_ShouldReturn400() throws Exception {
        // Arrange
        String malformedJson = "{ invalid json }";

        // Act & Assert
        mockMvc.perform(post("/api/age-groups")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(groupRepository, never()).save(any(Group.class));
    }
}
