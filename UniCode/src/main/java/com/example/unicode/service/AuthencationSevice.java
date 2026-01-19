package com.example.unicode.service;

import com.example.unicode.dto.request.LoginRequest;
import com.example.unicode.dto.request.LogoutRequest;
import com.example.unicode.dto.request.RefreshAccessTokenRequest;
import com.example.unicode.dto.response.LoginResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface AuthencationSevice {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse loginGoogle(OAuth2AuthenticationToken auth) throws JOSEException;
    String refreshAccessToken(RefreshAccessTokenRequest request) throws JOSEException;
    void Logout();
}
