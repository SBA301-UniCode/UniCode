package com.example.unicode.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigurationTest {

    @Test
    void openApiBeanShouldContainInfoAndBearerScheme() {
        SwaggerConfiguration cfg = new SwaggerConfiguration();
        var openApi = cfg.libraryManagementOpenAPI();

        assertNotNull(openApi.getInfo());
        assertEquals("Unicode  System API", openApi.getInfo().getTitle());
        assertNotNull(openApi.getComponents().getSecuritySchemes().get("bearerAuth"));
    }
}

