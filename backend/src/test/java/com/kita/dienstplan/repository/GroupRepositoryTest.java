package com.kita.dienstplan.repository;

import com.kita.dienstplan.entity.Group;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for GroupRepository custom query methods
 * Uses @DataJpaTest for real database interactions with H2 in-memory DB
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(TestJpaAuditingConfig.class)
@Sql(scripts = "/test-schema.sql")
class GroupRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GroupRepository groupRepository;

    private Group groupKaefer;
    private Group groupMarienkaefer;
    private Group groupSchmetterling;

    @BeforeEach
    void setUp() {
        // Create test groups
        groupKaefer = TestDataBuilder.createTestGroup("Käfer", "Käfer group description");
        groupMarienkaefer = TestDataBuilder.createTestGroup("Marienkäfer", "Marienkäfer group");
        groupSchmetterling = TestDataBuilder.createTestGroup("Schmetterling", "Schmetterling group");

        entityManager.persistAndFlush(groupKaefer);
        entityManager.persistAndFlush(groupMarienkaefer);
        entityManager.persistAndFlush(groupSchmetterling);
        entityManager.clear();
    }

    @Test
    void findByName_ExistingGroup_ShouldReturnGroup() {
        // Act
        Optional<Group> result = groupRepository.findByName("Käfer");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Käfer");
        assertThat(result.get().getDescription()).isEqualTo("Käfer group description");
    }

    @Test
    void findByName_NonExistingGroup_ShouldReturnEmpty() {
        // Act
        Optional<Group> result = groupRepository.findByName("NonExistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_NullName_ShouldReturnEmpty() {
        // Act
        Optional<Group> result = groupRepository.findByName(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_CaseSensitivity_ShouldBeCaseSensitive() {
        // Act
        Optional<Group> result = groupRepository.findByName("käfer"); // lowercase

        // Assert - Should not find "Käfer" (case sensitive)
        assertThat(result).isEmpty();
    }

    @Test
    void findByIsActiveTrueOrderByName_ShouldReturnAllActiveGroupsSorted() {
        // Act
        List<Group> result = groupRepository.findByIsActiveTrueOrderByName();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(Group::getName)
            .containsExactly("Käfer", "Marienkäfer", "Schmetterling"); // Alphabetical order
    }

    @Test
    void findByIsActiveTrueOrderByName_WithInactiveGroups_ShouldExcludeInactive() {
        // Arrange - Create inactive group
        Group inactiveGroup = TestDataBuilder.createInactiveGroup("Inactive Group");
        entityManager.persistAndFlush(inactiveGroup);

        // Act
        List<Group> result = groupRepository.findByIsActiveTrueOrderByName();

        // Assert
        assertThat(result).hasSize(3); // Should not include inactive group
        assertThat(result).extracting(Group::getName)
            .doesNotContain("Inactive Group");
    }

    @Test
    void findAllByOrderByName_ShouldReturnAllGroupsIncludingInactive() {
        // Arrange - Create inactive group
        Group inactiveGroup = TestDataBuilder.createInactiveGroup("Ameisen");
        entityManager.persistAndFlush(inactiveGroup);

        // Act
        List<Group> result = groupRepository.findAllByOrderByName();

        // Assert
        assertThat(result).hasSize(4); // 3 active + 1 inactive
        assertThat(result)
            .extracting(Group::getName)
            .containsExactly("Ameisen", "Käfer", "Marienkäfer", "Schmetterling"); // Alphabetical
    }

    @Test
    void findAllByOrderByName_EmptyDatabase_ShouldReturnEmptyList() {
        // Arrange - Clear database
        groupRepository.deleteAll();
        entityManager.flush();

        // Act
        List<Group> result = groupRepository.findAllByOrderByName();

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void existsByName_ExistingGroup_ShouldReturnTrue() {
        // Act
        boolean exists = groupRepository.existsByName("Käfer");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_NonExistingGroup_ShouldReturnFalse() {
        // Act
        boolean exists = groupRepository.existsByName("NonExistent");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void existsByName_InactiveGroup_ShouldStillReturnTrue() {
        // Arrange - Create inactive group
        Group inactiveGroup = TestDataBuilder.createInactiveGroup("Inactive Group");
        entityManager.persistAndFlush(inactiveGroup);

        // Act
        boolean exists = groupRepository.existsByName("Inactive Group");

        // Assert - Should find it even though it's inactive
        assertThat(exists).isTrue();
    }
}
