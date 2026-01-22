package com.example.unicode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    private String roleName;

    private String description;

    private Set<String> privilegeCodes;
}

