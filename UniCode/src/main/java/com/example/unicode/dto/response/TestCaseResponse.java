package com.example.unicode.dto.response;

import com.example.unicode.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponse {
    private UUID testcaseId;
    private String inputData;
    private String expectedOutput;
    private TestCase.OutputType outputType;
    private boolean hidden;
    private String description;
}
