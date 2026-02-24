package org.springframework.samples.petclinic.jhipster.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.samples.petclinic.jhipster.security.SecurityUtils;

/**
 * JPA auditing configuration.
 * Provides the current user login for @CreatedBy and @LastModifiedBy fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> springSecurityAuditorAware() {
        return () -> SecurityUtils.getCurrentUserLogin().or(() -> Optional.of("system"));
    }
}
