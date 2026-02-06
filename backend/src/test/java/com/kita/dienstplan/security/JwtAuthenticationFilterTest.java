package com.kita.dienstplan.security;

import com.kita.dienstplan.entity.Admin;
import com.kita.dienstplan.util.TestDataBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 * Tests JWT token extraction, validation, and authentication flow
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();

        testAdmin = TestDataBuilder.createTestAdmin("testuser", "password123", "Test User");
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testAdmin);
        when(jwtService.isTokenValid(validToken, testAdmin)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtService.extractUsername(invalidToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testAdmin);
        when(jwtService.isTokenValid(invalidToken, testAdmin)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String expiredToken = "expired.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(jwtService.extractUsername(expiredToken)).thenThrow(new RuntimeException("Token expired"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ForLoginEndpoint_ShouldSkipFilter() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/auth/login");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(request, never()).getHeader("Authorization");
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedAuthHeader_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat token");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    void doFilterInternal_WithBearerOnly_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNonExistentUser_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("nonexistent");
        when(userDetailsService.loadUserByUsername("nonexistent"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithAlreadyAuthenticatedUser_ShouldSkipAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";

        // Set up already authenticated context
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        testAdmin, null, testAdmin.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).isTokenValid(anyString(), any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyAuthHeader_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithTokenButNullUsername_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String token = "some.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidTokenForDifferentUser_ShouldAuthenticateCorrectUser() throws ServletException, IOException {
        // Arrange
        Admin admin2 = TestDataBuilder.createTestAdmin("alice", "password", "Alice Admin");
        String validToken = "alice.jwt.token";

        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("alice");
        when(userDetailsService.loadUserByUsername("alice")).thenReturn(admin2);
        when(jwtService.isTokenValid(validToken, admin2)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("alice", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldAlwaysCallFilterChain() throws ServletException, IOException {
        // This test ensures the filter chain is always called, even on errors

        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid");
        when(jwtService.extractUsername(anyString())).thenThrow(new RuntimeException("Parse error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithCaseSensitiveBearer_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("bearer valid.jwt.token"); // lowercase

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExtraSpacesInAuthHeader_ShouldExtractTokenCorrectly() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        when(request.getServletPath()).thenReturn("/api/staff");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.extractUsername(validToken)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testAdmin);
        when(jwtService.isTokenValid(validToken, testAdmin)).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, times(1)).extractUsername(validToken);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
