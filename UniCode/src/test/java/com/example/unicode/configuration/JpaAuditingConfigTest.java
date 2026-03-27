package com.example.unicode.configuration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaAuditingConfigTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorProviderShouldReturnSystemWhenUnauthenticated() {
        JpaAuditingConfig cfg = new JpaAuditingConfig();
        String auditor = cfg.auditorProvider().getCurrentAuditor().orElseThrow();

        assertEquals("SYSTEM", auditor);
    }

    @Test
    void auditorProviderShouldReturnUsernameWhenAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );
        JpaAuditingConfig cfg = new JpaAuditingConfig();
        String auditor = cfg.auditorProvider().getCurrentAuditor().orElseThrow();

        assertEquals("admin@test.com", auditor);
    }
}

