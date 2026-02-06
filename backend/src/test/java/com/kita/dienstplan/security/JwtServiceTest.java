package com.kita.dienstplan.security;

import com.kita.dienstplan.entity.Admin;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 * Tests token generation, validation, and claims extraction
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_SECRET = "test-secret-key-with-minimum-256-bits-for-testing-purposes-only-must-be-long";
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hour

    private UserDetails testUser;

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        // Create test user
        Admin admin = new Admin();
        admin.setUsername("testuser");
        admin.setPassword("password");
        admin.setFullName("Test User");
        admin.setIsActive(true);
        testUser = admin;
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Act
        String token = jwtService.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT has 3 parts separated by dots

        // Verify token structure (header.payload.signature)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts");
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        boolean isValid = jwtService.isTokenValid(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        // Arrange - Create an already-expired token
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Negative expiration
        String expiredToken = jwtService.generateToken(testUser);

        // Reset expiration for validation
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        // Act & Assert
        // Expired tokens throw ExpiredJwtException during extraction
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(expiredToken, testUser);
        });
    }

    @Test
    void isTokenValid_WithWrongUser_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        Admin differentUser = new Admin();
        differentUser.setUsername("differentuser");
        differentUser.setPassword("password");
        differentUser.setIsActive(true);

        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithMalformedToken_ShouldThrowException() {
        // Arrange
        String malformedToken = "this.is.not.a.valid.jwt";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(malformedToken, testUser);
        });
    }

    @Test
    void isTokenValid_WithEmptyToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid("", testUser);
        });
    }

    @Test
    void isTokenValid_WithNullToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(null, testUser);
        });
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Assert
        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldIncludeClaims() {
        // Arrange
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");
        extraClaims.put("userId", 123L);

        // Act
        String token = jwtService.generateToken(extraClaims, testUser);

        // Assert
        assertNotNull(token);

        // Parse token and verify extra claims
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("ADMIN", claims.get("role"));
        assertEquals(123, claims.get("userId"));
    }

    @Test
    void generateToken_ShouldSetCorrectExpiration() {
        // Arrange
        long beforeGeneration = System.currentTimeMillis();

        // Act
        String token = jwtService.generateToken(testUser);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Assert
        long expectedExpiration = beforeGeneration + TEST_EXPIRATION;
        long actualExpiration = expiration.getTime();

        // Allow 1 second tolerance
        assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000,
                "Expiration should be approximately " + TEST_EXPIRATION + "ms from now");
    }

    @Test
    void generateToken_ShouldSetIssuedAtToCurrentTime() {
        // Arrange
        long beforeGeneration = System.currentTimeMillis();

        // Act
        String token = jwtService.generateToken(testUser);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        // Assert
        long actualIssuedAt = issuedAt.getTime();
        assertTrue(Math.abs(actualIssuedAt - beforeGeneration) < 1000,
                "IssuedAt should be approximately current time");
    }

    @Test
    void generateToken_ShouldSetSubjectToUsername() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Act
        SecretKey key = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Assert
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void extractUsername_FromDifferentTokens_ShouldReturnCorrectUsernames() {
        // Arrange
        Admin user1 = new Admin();
        user1.setUsername("alice");
        user1.setPassword("password");
        user1.setIsActive(true);

        Admin user2 = new Admin();
        user2.setUsername("bob");
        user2.setPassword("password");
        user2.setIsActive(true);

        // Act
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        String username1 = jwtService.extractUsername(token1);
        String username2 = jwtService.extractUsername(token2);

        // Assert
        assertEquals("alice", username1);
        assertEquals("bob", username2);
        assertNotEquals(username1, username2);
    }

    @Test
    void isTokenValid_WithTokenForDifferentSecret_ShouldThrowException() {
        // Arrange
        String token = jwtService.generateToken(testUser);

        // Change the secret key
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "different-secret-key-with-minimum-256-bits-for-testing-purposes-only-must-be-long");

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(token);
        }, "Token signed with different key should fail validation");
    }

    @Test
    void generateToken_MultipleTimes_ShouldAllBeValid() {
        // Act - Generate multiple tokens
        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(testUser);
        String token3 = jwtService.generateToken(testUser);

        // Assert - All tokens should be valid for the same user
        assertTrue(jwtService.isTokenValid(token1, testUser));
        assertTrue(jwtService.isTokenValid(token2, testUser));
        assertTrue(jwtService.isTokenValid(token3, testUser));

        // All should extract the same username
        assertEquals("testuser", jwtService.extractUsername(token1));
        assertEquals("testuser", jwtService.extractUsername(token2));
        assertEquals("testuser", jwtService.extractUsername(token3));
    }
}
