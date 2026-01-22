package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;

    private String email;

    private String name;

    private String avatarUrl;

    private boolean isActive;

    private LocalDateTime createdAt;

    private Set<RoleResponse> roles;
}

