package com.example.unicode.service;

import com.example.unicode.entity.Users;
import com.nimbusds.jose.JOSEException;

public interface RefreshTokenService {
    String generateRefreshToken(Users user);
    String refreshAccessToken(String refreshToken) throws JOSEException;
}
