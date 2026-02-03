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
import com.example.unicode.service.AuthencationSevice;
import com.example.unicode.service.RefreshTokenService;
import com.example.unicode.service.TokenService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthencationServiceImpl implements AuthencationSevice {
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Users user = usersRepository.findByEmail(loginRequest.getUsername());
        if (user == null) {
            throw new AppException(ErrorCode.INVALID_LOGIN_REQUEST);
        }
        if (!user.isActive()) {
            throw new AppException(ErrorCode.USER_INACTIVE);
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_LOGIN_REQUEST);
        }
        try {
            return LoginResponse.builder()
                    .accessToken(tokenService.generateToken(user))
                    .refreshToken(refreshTokenService.generateRefreshToken(user))
                    .build();
        } catch (Exception e) {
            throw new JwtException("Generate token failed", e);
        }
    }

    @Override
    public LoginResponse loginGoogle(OAuth2AuthenticationToken auth) throws JOSEException {
        Users user = usersRepository.findByEmail(auth.getPrincipal().getAttribute("email"));
        if (user == null) {
            Role learnerRole = roleRepository.findByRoleCodeAndDeletedFalse("LEARNER")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user = Users.builder()
                    .email(auth.getPrincipal().getAttribute("email"))
                    .name(auth.getPrincipal().getAttribute("name"))
                    .avatarUrl(auth.getPrincipal().getAttribute("picture"))
                    .rolesList(Set.of(learnerRole))
                    .build();
            usersRepository.save(user);
        }
        return LoginResponse.builder()
                .accessToken(tokenService.generateToken(user))
                .refreshToken(refreshTokenService.generateRefreshToken(user))
                .build();
    }

    @Override
    public String refreshAccessToken(RefreshAccessTokenRequest refreshToken) throws JOSEException {
        return refreshTokenService.refreshAccessToken(refreshToken.getRefreshToken());
    }

    @Override
    public void Logout() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} is logging out", email);
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        user.setTokenVersion(user.getTokenVersion() + 1);
        usersRepository.save(user);

    }
}
