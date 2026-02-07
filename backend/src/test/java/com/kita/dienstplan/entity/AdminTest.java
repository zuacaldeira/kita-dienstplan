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

    // ==================== LOMBOK GENERATED METHOD TESTS ====================

    @Test
    void noArgsConstructor_ShouldCreateEmptyAdmin() {
        // Act
        Admin newAdmin = new Admin();

        // Assert
        assertNotNull(newAdmin);
        assertNull(newAdmin.getId());
        assertNull(newAdmin.getUsername());
        assertNull(newAdmin.getPassword());
    }

    @Test
    void allArgsConstructor_ShouldSetAllFields() {
        // Arrange
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime lastLogin = now.minusDays(1);

        // Act
        Admin newAdmin = new Admin(
            1L, "adminuser", "password", "Admin User",
            "admin@example.com", true, lastLogin, now, now
        );

        // Assert
        assertEquals(1L, newAdmin.getId());
        assertEquals("adminuser", newAdmin.getUsername());
        assertEquals("password", newAdmin.getPassword());
        assertEquals("Admin User", newAdmin.getFullName());
        assertEquals("admin@example.com", newAdmin.getEmail());
        assertTrue(newAdmin.getIsActive());
        assertEquals(lastLogin, newAdmin.getLastLogin());
    }

    @Test
    void setId_ShouldUpdateId() {
        // Act
        admin.setId(99L);

        // Assert
        assertEquals(99L, admin.getId());
    }

    @Test
    void setUsername_ShouldUpdateUsername() {
        // Act
        admin.setUsername("newusername");

        // Assert
        assertEquals("newusername", admin.getUsername());
    }

    @Test
    void setPassword_ShouldUpdatePassword() {
        // Act
        admin.setPassword("newHashedPassword");

        // Assert
        assertEquals("newHashedPassword", admin.getPassword());
    }

    @Test
    void setEmail_ShouldUpdateEmail() {
        // Act
        admin.setEmail("newemail@example.com");

        // Assert
        assertEquals("newemail@example.com", admin.getEmail());
    }

    @Test
    void equals_ShouldReturnTrueForSameId() {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setId(1L);
        Admin admin2 = new Admin();
        admin2.setId(1L);

        // Act & Assert
        assertEquals(admin1, admin2);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentId() {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setId(1L);
        Admin admin2 = new Admin();
        admin2.setId(2L);

        // Act & Assert
        assertNotEquals(admin1, admin2);
    }

    @Test
    void equals_ShouldReturnTrueForSameInstance() {
        // Act & Assert
        assertEquals(admin, admin);
    }

    @Test
    void equals_ShouldReturnFalseForNull() {
        // Act & Assert
        assertNotEquals(admin, null);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        // Act
        int hash1 = admin.hashCode();
        int hash2 = admin.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_ShouldBeEqualForEqualObjects() {
        // Arrange
        Admin admin1 = new Admin();
        admin1.setId(1L);
        Admin admin2 = new Admin();
        admin2.setId(1L);

        // Act & Assert
        assertEquals(admin1.hashCode(), admin2.hashCode());
    }

    @Test
    void toString_ShouldContainKeyFields() {
        // Arrange
        admin.setId(1L);
        admin.setUsername("testuser");

        // Act
        String result = admin.toString();

        // Assert
        assertTrue(result.contains("Admin"));
        assertTrue(result.contains("id=1"));
        assertTrue(result.contains("testuser"));
    }
}
