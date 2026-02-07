package com.kita.dienstplan.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kita.dienstplan.entity.Admin;
import com.kita.dienstplan.repository.AdminRepository;
import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for full authentication flow
 * Uses full Spring context with @SpringBootTest
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Admin testAdmin;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        // Create test admin in database
        testAdmin = TestDataBuilder.createTestAdmin(TEST_USERNAME, TEST_PASSWORD, "Test User");
        adminRepository.save(testAdmin);
    }

    @Test
    void fullAuthFlow_LoginAndAccessProtectedEndpoint_ShouldSucceed() throws Exception {
        // Step 1: Login via REST API
        String loginRequest = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(TEST_USERNAME, TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();

        // Extract token from response
        String responseBody = loginResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        String token = (String) responseMap.get("token");

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");

        // Step 2: Access protected endpoint with token
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.email").value(TEST_USERNAME + "@kita-casa-azul.de"));
    }

    @Test
    void fullAuthFlow_LoginWithInvalidCredentials_ShouldFail() throws Exception {
        // Attempt login with wrong password
        String loginRequest = """
            {
                "username": "%s",
                "password": "wrongpassword"
            }
            """.formatted(TEST_USERNAME);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void fullAuthFlow_AccessProtectedEndpointWithoutToken_ShouldReturn403() throws Exception {
        // Attempt to access protected endpoint without token
        // Spring Security returns 403 Forbidden for missing/invalid JWT tokens
        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isForbidden());
    }

    @Test
    void fullAuthFlow_AccessProtectedEndpointWithInvalidToken_ShouldReturn403() throws Exception {
        // Attempt to access protected endpoint with invalid token
        // Spring Security returns 403 Forbidden for invalid JWT tokens
        mockMvc.perform(get("/api/staff")
                        .header("Authorization", "Bearer invalid-token-12345"))
                .andExpect(status().isForbidden());
    }

    @Test
    void fullAuthFlow_LoginUpdatesLastLoginTime() throws Exception {
        // Check lastLogin is null initially
        Admin adminBefore = adminRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertNull(adminBefore.getLastLogin(), "LastLogin should be null before first login");

        // Login
        String loginRequest = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(TEST_USERNAME, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk());

        // Verify lastLogin was updated
        Admin adminAfter = adminRepository.findByUsername(TEST_USERNAME).orElseThrow();
        assertNotNull(adminAfter.getLastLogin(), "LastLogin should be set after login");
    }

    @Test
    void fullAuthFlow_InactiveUser_ShouldNotBeAbleToLogin() throws Exception {
        // Deactivate user
        testAdmin.setIsActive(false);
        adminRepository.save(testAdmin);

        // Attempt login
        String loginRequest = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(TEST_USERNAME, TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void fullAuthFlow_MultipleLoginsShouldGenerateDifferentTokens() throws Exception {
        // First login
        String loginRequest = """
            {
                "username": "%s",
                "password": "%s"
            }
            """.formatted(TEST_USERNAME, TEST_PASSWORD);

        MvcResult result1 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String response1 = result1.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> map1 = objectMapper.readValue(response1, Map.class);
        String token1 = (String) map1.get("token");

        // Wait a moment to ensure different timestamps
        Thread.sleep(1000);

        // Second login
        MvcResult result2 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        String response2 = result2.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> map2 = objectMapper.readValue(response2, Map.class);
        String token2 = (String) map2.get("token");

        // Both tokens should be valid but different
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2, "Different login attempts should generate different tokens");

        // Both tokens should work
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk());
    }
}
