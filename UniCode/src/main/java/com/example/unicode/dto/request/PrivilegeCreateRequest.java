package com.example.unicode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeCreateRequest {

    @NotBlank(message = "Privilege code is required")
    private String privilegeCode;

    @NotBlank(message = "Privilege name is required")
    private String privilegeName;

    private String description;
}

