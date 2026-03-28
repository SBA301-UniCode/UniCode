package com.example.unicode.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PasswordAndWatermarkConfigTest {

    @Test
    void configBeansShouldBeCreatable() {
        PasswordConfig p = new PasswordConfig();
        WatermarkConfig w = new WatermarkConfig();

        assertNotNull(p.passwordEncoder());
        assertNotNull(w.watermarkEngine());
    }
}

