package com.example.unicode.configuration;

import com.example.unicode.watermark.WatermarkEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WatermarkConfig {

    @Bean
    public WatermarkEngine watermarkEngine() {
        return new WatermarkEngine();
    }
}
