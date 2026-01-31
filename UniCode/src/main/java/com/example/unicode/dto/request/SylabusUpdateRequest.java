package com.example.unicode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SylabusUpdateRequest {

    private String courseContent;

    private String method;

    private String referenceMaterial;
}
