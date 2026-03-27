package com.example.unicode.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String roleCode;
}
