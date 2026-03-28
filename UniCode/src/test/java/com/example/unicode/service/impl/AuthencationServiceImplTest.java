package com.example.unicode.service.impl;

import com.example.unicode.dto.request.LoginRequest;
import com.example.unicode.dto.request.RefreshAccessTokenRequest;
import com.example.unicode.dto.response.LoginResponse;
import com.example.unicode.entity.Role;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.repository.RoleRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.RefreshTokenService;
import com.example.unicode.service.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthencationServiceImplTest {

    @Mock
    private TokenService tokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthencationServiceImpl authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loginShouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("nope@test.com");
        request.setPassword("123");
        when(usersRepository.findByEmail("nope@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));

        assertEquals(ErrorCode.INVALID_LOGIN_REQUEST, ex.getErrorCode());
    }

    @Test
    void loginShouldThrowWhenPasswordNotMatch() {
        Users user = Users.builder().email("u@test.com").password("hashed").build();
        LoginRequest request = new LoginRequest();
        request.setUsername("u@test.com");
        request.setPassword("wrong");

        when(usersRepository.findByEmail("u@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> authService.login(request));

        assertEquals(ErrorCode.INVALID_LOGIN_REQUEST, ex.getErrorCode());
    }

    @Test
    void loginShouldReturnTokensWhenCredentialsAreValid() throws Exception {
        Users user = Users.builder().email("u@test.com").password("hashed").build();
        LoginRequest request = new LoginRequest();
        request.setUsername("u@test.com");
        request.setPassword("secret");

        when(usersRepository.findByEmail("u@test.com")).thenReturn(user);
        when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("access");
        when(refreshTokenService.generateRefreshToken(user)).thenReturn("refresh");

        LoginResponse response = authService.login(request);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void loginGoogleShouldCreateUserWhenNotExists() throws Exception {
        OAuth2AuthenticationToken auth = buildGoogleAuth("new@test.com", "New User", "avatar.png");
        Role learnerRole = new Role();
        learnerRole.setRoleCode("LEARNER");

        when(usersRepository.findByEmail("new@test.com")).thenReturn(null);
        when(roleRepository.findByRoleCodeAndDeletedFalse("LEARNER")).thenReturn(Optional.of(learnerRole));
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.generateToken(any(Users.class))).thenReturn("access");
        when(refreshTokenService.generateRefreshToken(any(Users.class))).thenReturn("refresh");

        LoginResponse response = authService.loginGoogle(auth);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        verify(usersRepository).save(any(Users.class));
    }

    @Test
    void refreshAccessTokenShouldDelegateToRefreshTokenService() throws Exception {
        RefreshAccessTokenRequest request = new RefreshAccessTokenRequest();
        request.setRefreshToken("rf");
        when(refreshTokenService.refreshAccessToken("rf")).thenReturn("new-access");

        String accessToken = authService.refreshAccessToken(request);

        assertEquals("new-access", accessToken);
    }

    @Test
    void logoutShouldIncreaseTokenVersion() {
        Users user = Users.builder().email("u@test.com").tokenVersion(1).build();
        when(usersRepository.findByEmail("u@test.com")).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("u@test.com", "pwd", List.of()));

        authService.Logout();

        assertEquals(2, user.getTokenVersion());
        verify(usersRepository).save(user);
    }

    private OAuth2AuthenticationToken buildGoogleAuth(String email, String name, String picture) {
        OAuth2User user = new DefaultOAuth2User(
                List.of(),
                Map.of("email", email, "name", name, "picture", picture),
                "email"
        );
        return new OAuth2AuthenticationToken(user, List.of(), "google");
    }
}
