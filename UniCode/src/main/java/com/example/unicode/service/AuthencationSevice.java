package com.example.unicode.service;

import com.example.unicode.dto.request.LoginRequest;
import com.example.unicode.dto.request.LogoutRequest;
import com.example.unicode.dto.request.RefreshAccessTokenRequest;
import com.example.unicode.dto.response.LoginResponse;
import com.nimbusds.jose.JOSEException;

public interface AuthencationSevice {
    LoginResponse login(LoginRequest loginRequest);
    String refreshAccessToken(RefreshAccessTokenRequest request) throws JOSEException;
    void Logout();
}
