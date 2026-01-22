package com.example.unicode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String name;

    private String avatarUrl;

    private Boolean isActive;

    private Set<String> roleCodes;
}

