package com.kita.dienstplan.entity;

import com.kita.dienstplan.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Admin entity
 * Tests UserDetails implementation for Spring Security integration
 */
class AdminTest {

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = TestDataBuilder.createTestAdmin("testuser", "password123", "Test Admin");
    }

    @Test
    void getAuthorities_ShouldReturnRoleAdmin() {
        // Act
        Collection<? extends GrantedAuthority> authorities = admin.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getUsername_ShouldReturnUsername() {
        // Act
        String username = admin.getUsername();

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void getPassword_ShouldReturnEncodedPassword() {
        // Act
        String password = admin.getPassword();

        // Assert
        assertNotNull(password);
        assertTrue(password.startsWith("$2a$") || password.startsWith("$2b$"),
                "Password should be BCrypt encoded");
    }

    @Test
    void isEnabled_WhenActive_ShouldReturnTrue() {
        // Arrange
        admin.setIsActive(true);

        // Act & Assert
        assertTrue(admin.isEnabled(), "Active admin should be enabled");
    }

    @Test
    void isEnabled_WhenInactive_ShouldReturnFalse() {
        // Arrange
        admin.setIsActive(false);

        // Act & Assert
        assertFalse(admin.isEnabled(), "Inactive admin should NOT be enabled");
    }

    @Test
    void isAccountNonExpired_ShouldAlwaysReturnTrue() {
        // Act & Assert
        assertTrue(admin.isAccountNonExpired(), "Accounts do not expire");
    }

    @Test
    void isAccountNonLocked_ShouldAlwaysReturnTrue() {
        // Act & Assert
        assertTrue(admin.isAccountNonLocked(), "Accounts are not locked");
    }

    @Test
    void isCredentialsNonExpired_ShouldAlwaysReturnTrue() {
        // Act & Assert
        assertTrue(admin.isCredentialsNonExpired(), "Credentials do not expire");
    }

    @Test
    void createAdmin_WithAllFields_ShouldSetFieldsCorrectly() {
        // Arrange & Act
        Admin newAdmin = new Admin();
        newAdmin.setId(1L);
        newAdmin.setUsername("alexandre");
        newAdmin.setPassword("encoded-password");
        newAdmin.setFullName("Alexandre Doe");
        newAdmin.setEmail("alexandre@kita.de");
        newAdmin.setIsActive(true);

        // Assert
        assertEquals(1L, newAdmin.getId());
        assertEquals("alexandre", newAdmin.getUsername());
        assertEquals("encoded-password", newAdmin.getPassword());
        assertEquals("Alexandre Doe", newAdmin.getFullName());
        assertEquals("alexandre@kita.de", newAdmin.getEmail());
        assertTrue(newAdmin.getIsActive());
    }

    @Test
    void isActive_DefaultValue_ShouldBeTrue() {
        // Arrange
        Admin newAdmin = new Admin();

        // Act & Assert
        assertTrue(newAdmin.getIsActive(), "Default isActive should be true");
    }

    @Test
    void multipleAdmins_ShouldHaveSameRole() {
        // Arrange
        Admin admin1 = TestDataBuilder.createTestAdmin("user1", "pass1", "User One");
        Admin admin2 = TestDataBuilder.createTestAdmin("user2", "pass2", "User Two");

        // Act
        Collection<? extends GrantedAuthority> auth1 = admin1.getAuthorities();
        Collection<? extends GrantedAuthority> auth2 = admin2.getAuthorities();

        // Assert
        assertEquals(auth1, auth2, "All admins should have the same ROLE_ADMIN");
    }

    @Test
    void inactiveAdmin_ShouldNotBeEnabled() {
        // Arrange
        Admin inactiveAdmin = TestDataBuilder.createInactiveAdmin("inactive", "password");

        // Act & Assert
        assertFalse(inactiveAdmin.isEnabled());
        assertFalse(inactiveAdmin.getIsActive());
    }

    @Test
    void toggleActive_ShouldAffectIsEnabled() {
        // Arrange
        admin.setIsActive(true);
        assertTrue(admin.isEnabled());

        // Act - Toggle to inactive
        admin.setIsActive(false);

        // Assert
        assertFalse(admin.isEnabled());

        // Act - Toggle back to active
        admin.setIsActive(true);

        // Assert
        assertTrue(admin.isEnabled());
    }

    @Test
    void admin_ShouldImplementUserDetails() {
        // Assert
        assertTrue(admin instanceof org.springframework.security.core.userdetails.UserDetails,
                "Admin should implement UserDetails interface");
    }

    @Test
    void getFullName_ShouldReturnCorrectName() {
        // Act
        String fullName = admin.getFullName();

        // Assert
        assertEquals("Test Admin", fullName);
    }

    @Test
    void setLastLogin_ShouldUpdateField() {
        // Arrange
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Act
        admin.setLastLogin(now);

        // Assert
        assertEquals(now, admin.getLastLogin());
    }
}
