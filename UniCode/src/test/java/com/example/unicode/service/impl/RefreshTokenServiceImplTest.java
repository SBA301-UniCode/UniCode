package com.example.unicode.service.impl;

import com.example.unicode.entity.RefreshToken;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private TokenServiceImpl tokenService;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(tokenService, refreshTokenRepository);
        ReflectionTestUtils.setField(refreshTokenService, "REFRESH_EXPIRATION_TIME", 3600L);
    }

    @Test
    void generateRefreshTokenShouldReplaceOldTokensAndSaveNewOne() {
        Users user = new Users();
        doReturn(List.of(new RefreshToken())).when(refreshTokenRepository).findAllByUser(user);

        String token = refreshTokenService.generateRefreshToken(user);

        assertNotNull(token);
        verify(refreshTokenRepository).deleteAll(anyList());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refreshAccessTokenShouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken("missing")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> refreshTokenService.refreshAccessToken("missing"));

        assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void refreshAccessTokenShouldThrowWhenTokenExpired() {
        RefreshToken refreshToken = RefreshToken.builder()
                .token("expired")
                .expiryDate(LocalDateTime.now().minusMinutes(1))
                .user(new Users())
                .build();
        when(refreshTokenRepository.findByToken("expired")).thenReturn(refreshToken);

        AppException ex = assertThrows(AppException.class, () -> refreshTokenService.refreshAccessToken("expired"));

        assertEquals(ErrorCode.REFRESH_TOKEN_EXPIRED, ex.getErrorCode());
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void refreshAccessTokenShouldGenerateNewAccessToken() throws Exception {
        Users user = new Users();
        RefreshToken refreshToken = RefreshToken.builder()
                .token("ok")
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        when(refreshTokenRepository.findByToken("ok")).thenReturn(refreshToken);
        when(tokenService.generateToken(user)).thenReturn("new-access-token");

        String accessToken = refreshTokenService.refreshAccessToken("ok");

        assertEquals("new-access-token", accessToken);
    }
}
