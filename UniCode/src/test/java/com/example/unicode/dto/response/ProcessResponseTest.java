package com.example.unicode.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessResponseTest {

    @Test
    void shouldInstantiateEmptyDto() {
        ProcessResponse dto = new ProcessResponse();
        assertNotNull(dto);
        String className = ProcessResponse.class.getName();
        assertTrue(className.contains("ProcessResponse"));
    }
}
