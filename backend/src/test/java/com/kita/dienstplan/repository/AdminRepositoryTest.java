package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Admin;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for AdminRepository custom query methods
 * Security-critical tests for authentication queries
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
@Sql(scripts = "/test-schema.sql")
class AdminRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AdminRepository adminRepository;

    private Admin activeAdmin;
    private Admin inactiveAdmin;

    @BeforeEach
    void setUp() {
        // Create test admins
        activeAdmin = TestDataBuilder.createTestAdmin("testadmin", "password123", "Test Admin");
        inactiveAdmin = TestDataBuilder.createInactiveAdmin("inactiveadmin", "password456");

        entityManager.persistAndFlush(activeAdmin);
        entityManager.persistAndFlush(inactiveAdmin);
        entityManager.clear();
    }

    @Test
    void findByUsername_ExistingAdmin_ShouldReturnAdmin() {
        // Act
        Optional<Admin> result = adminRepository.findByUsername("testadmin");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testadmin");
        assertThat(result.get().getFullName()).isEqualTo("Test Admin");
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    void findByUsername_NonExistingAdmin_ShouldReturnEmpty() {
        // Act
        Optional<Admin> result = adminRepository.findByUsername("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_NullUsername_ShouldReturnEmpty() {
        // Act
        Optional<Admin> result = adminRepository.findByUsername(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_InactiveAdmin_ShouldStillReturnAdmin() {
        // Act - findByUsername does NOT filter by isActive
        Optional<Admin> result = adminRepository.findByUsername("inactiveadmin");

        // Assert - Should find inactive admin
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("inactiveadmin");
        assertThat(result.get().getIsActive()).isFalse();
    }

    @Test
    void existsByUsername_ExistingAdmin_ShouldReturnTrue() {
        // Act
        boolean exists = adminRepository.existsByUsername("testadmin");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_NonExistingAdmin_ShouldReturnFalse() {
        // Act
        boolean exists = adminRepository.existsByUsername("nonexistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void existsByUsername_InactiveAdmin_ShouldStillReturnTrue() {
        // Act - existsByUsername does NOT filter by isActive
        boolean exists = adminRepository.existsByUsername("inactiveadmin");

        // Assert - Should find inactive admin for duplicate detection
        assertThat(exists).isTrue();
    }

    @Test
    void findByUsernameAndIsActiveTrue_ActiveAdmin_ShouldReturnAdmin() {
        // Act
        Optional<Admin> result = adminRepository.findByUsernameAndIsActiveTrue("testadmin");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testadmin");
        assertThat(result.get().getIsActive()).isTrue();
    }

    @Test
    void findByUsernameAndIsActiveTrue_InactiveAdmin_ShouldReturnEmpty() {
        // Act - Security-critical: inactive admins should NOT be able to login
        Optional<Admin> result = adminRepository.findByUsernameAndIsActiveTrue("inactiveadmin");

        // Assert - Should NOT find inactive admin
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsernameAndIsActiveTrue_NonExistingAdmin_ShouldReturnEmpty() {
        // Act
        Optional<Admin> result = adminRepository.findByUsernameAndIsActiveTrue("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsernameAndIsActiveTrue_NullUsername_ShouldReturnEmpty() {
        // Act
        Optional<Admin> result = adminRepository.findByUsernameAndIsActiveTrue(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void passwordEncryption_ShouldUseBCrypt() {
        // Arrange
        Admin admin = adminRepository.findByUsername("testadmin").orElseThrow();

        // Assert - Password should be BCrypt encoded
        assertThat(admin.getPassword()).startsWith("$2a$");
        assertThat(admin.getPassword()).hasSize(60); // BCrypt hash length
    }
}
