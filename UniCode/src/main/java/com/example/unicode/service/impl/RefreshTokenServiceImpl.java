package com.example.unicode.service.impl;

import com.example.unicode.entity.RefreshToken;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.repository.RefreshTokenRepository;
import com.example.unicode.service.RefreshTokenService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final TokenServiceImpl tokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.refreshExpiration}")
    private Long REFRESH_EXPIRATION_TIME;

    @Override
    public String generateRefreshToken(Users user) {
        refreshTokenRepository.deleteAll(refreshTokenRepository.findAllByUser(user));
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_EXPIRATION_TIME))
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws JOSEException {
        RefreshToken rf = refreshTokenRepository.findByToken(refreshToken);
        if (rf == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if (rf.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(rf);
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        return tokenService.generateToken(rf.getUser());
    }
}
