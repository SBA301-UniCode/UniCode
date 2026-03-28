package com.example.unicode.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class AICodeAssistantRequest {

    private String code;
    private String language;
    private String description;

    /**
     * Mode: hint | explain_error | review | suggest_fix
     */
    private String mode;

    private List<TestResultItem> testResults;

    @Data
    public static class TestResultItem {
        private String input;
        private String expected;
        private String actual;
        private boolean passed;
    }
}
