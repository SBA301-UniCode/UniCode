package com.example.unicode.configuration;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaConfigTest {

    @Test
    void basicMetadataShouldMatchExpectedValues() {
        JavaConfig cfg = new JavaConfig();

        assertEquals("eclipse-temurin:17-alpine", cfg.getDockerImage());
        assertEquals("Main.java", cfg.getFileName());
        assertTrue(cfg.getCompileCmd().contains("javac"));
        assertTrue(cfg.getRunCmd().contains("java"));
    }

    @Test
    void wrapCodeShouldGenerateMainAndParamParsers() {
        JavaConfig cfg = new JavaConfig();
        String code = cfg.wrapCode("class Solution { public int solve(int a){ return a; } }",
                List.of("int", "String", "int[]", "Map<String,Integer>", "List<List<Integer>>"),
                "solve");

        assertTrue(code.contains("public class Main"));
        assertTrue(code.contains("int p0 = toInt(inputs.get(0))"));
        assertTrue(code.contains("String p1 = inputs.get(1).getAsString()"));
        assertTrue(code.contains("int[] p2 = toIntArray(inputs.get(2))"));
        assertTrue(code.contains("Map<String, Integer> p3"));
        assertTrue(code.contains("List<List<Integer>> p4"));
        assertTrue(code.contains("method.getName().equals(\"solve\")"));
    }
}

