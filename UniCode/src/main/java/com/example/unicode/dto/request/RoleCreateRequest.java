package com.example.unicode.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateRequest {

    @NotBlank(message = "Role code is required")
    private String roleCode;

    @NotBlank(message = "Role name is required")
    private String roleName;

    private String description;

    private Set<String> privilegeCodes;
}

