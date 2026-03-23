package com.example.unicode.dto.request;

import com.example.unicode.entity.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseRequest {
    private String inputData;
    private String expectedOutput;
    private TestCase.OutputType outputType;
    private boolean hidden;
    private String description;
}