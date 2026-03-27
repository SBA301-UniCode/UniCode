package com.example.unicode.service.impl;

import com.example.unicode.entity.Privilege;
import com.example.unicode.entity.Role;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.repository.UsersRepository;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(usersRepository);
        ReflectionTestUtils.setField(tokenService, "SECRET_KEY", "1234567890123456789012345678901234567890123456789012345678901234");
        ReflectionTestUtils.setField(tokenService, "EXPIRATION_TIME", 3600000L);
    }

    @Test
    void generateTokenShouldPersistJwtIdAndReturnSignedToken() throws Exception {
        Users user = new Users();
        user.setEmail("user@test.com");
        user.setTokenVersion(2);

        Role role = new Role();
        role.setRoleCode("LEARNER");
        Privilege privilege = new Privilege("COURSE_READ", "Read", "Read course");
        role.setPrivileges(Set.of(privilege));
        user.setRolesList(Set.of(role));

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertNotNull(user.getJwtId());
        verify(usersRepository).save(user);

        SignedJWT signedJWT = SignedJWT.parse(token);
        assertEquals("user@test.com", signedJWT.getJWTClaimsSet().getSubject());
        assertEquals(user.getJwtId(), signedJWT.getJWTClaimsSet().getJWTID());
        assertEquals(2, signedJWT.getJWTClaimsSet().getIntegerClaim("tokenVersion"));
        assertTrue(signedJWT.getJWTClaimsSet().getStringClaim("scope").contains("ROLE_LEARNER"));
        assertTrue(signedJWT.getJWTClaimsSet().getStringClaim("scope").contains("COURSE_READ"));
    }

    @Test
    void validateTokenShouldReturnTrueWhenTokenIsValid() throws Exception {
        Users user = new Users();
        user.setEmail("user@test.com");
        user.setTokenVersion(1);
        user.setRolesList(Set.of());

        String token = tokenService.generateToken(user);
        when(usersRepository.findByEmail("user@test.com")).thenReturn(user);

        boolean valid = tokenService.validateToken(token);

        assertTrue(valid);
    }

    @Test
    void validateTokenShouldReturnFalseWhenJwtIdChanged() throws Exception {
        Users user = new Users();
        user.setEmail("user@test.com");
        user.setTokenVersion(1);
        user.setRolesList(Set.of());

        String token = tokenService.generateToken(user);

        Users dbUser = new Users();
        dbUser.setEmail("user@test.com");
        dbUser.setTokenVersion(1);
        dbUser.setJwtId("another-jti");
        when(usersRepository.findByEmail("user@test.com")).thenReturn(dbUser);

        boolean valid = tokenService.validateToken(token);

        assertFalse(valid);
    }

    @Test
    void validateTokenShouldThrowWhenUserNotFound() throws Exception {
        Users user = new Users();
        user.setEmail("missing@test.com");
        user.setTokenVersion(1);

        String token = tokenService.generateToken(user);
        when(usersRepository.findByEmail("missing@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> tokenService.validateToken(token));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }
}
