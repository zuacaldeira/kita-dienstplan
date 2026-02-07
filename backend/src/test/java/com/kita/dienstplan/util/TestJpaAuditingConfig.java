package com.kita.dienstplan.util;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Test configuration for JPA auditing
 * Provides a mock auditor for createdBy/updatedBy fields
 */
@TestConfiguration
@EnableJpaAuditing
public class TestJpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of("test-user");
    }
}
