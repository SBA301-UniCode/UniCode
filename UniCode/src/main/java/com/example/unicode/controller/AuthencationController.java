package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.LoginRequest;
import com.example.unicode.dto.request.RefreshAccessTokenRequest;
import com.example.unicode.service.AuthencationSevice;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthencationController {
    private final AuthencationSevice authencationSevice;
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest loginRequest)
    {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        return ApiResponse.success(authencationSevice.login(loginRequest));
    }

   @PostMapping("/refresh-access-token")
    public ApiResponse refreshAccessToken(@RequestBody RefreshAccessTokenRequest refreshAccessTokenRequest) throws JOSEException {

            String newAccessToken = authencationSevice.refreshAccessToken(refreshAccessTokenRequest);
            return ApiResponse.success(newAccessToken);

    }
    @GetMapping("/logout")
    public ApiResponse logout()
    {
        authencationSevice.Logout();
        return ApiResponse.success("Logout successful");
    }
    @GetMapping("/test")
    public ApiResponse test()
    {
        return ApiResponse.success("Test successful");
    }
}
