package com.example.unicode.exception;

import com.example.unicode.base.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionTest {

    private final GlobalException handler = new GlobalException();

    @Test
    void handleAppExceptionShouldUseErrorCodeStatus() {
        var response = handler.handleAppException(new AppException(ErrorCode.USER_NOT_FOUND));

        assertEquals(ErrorCode.USER_NOT_FOUND.getStatus().value(), response.getStatusCode().value());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void handleJwtExceptionShouldReturnUnauthorized() {
        var response = handler.handleJwtException(new JwtException("invalid"));

        assertEquals(401, response.getStatusCode().value());
        assertEquals("invalid", response.getBody().getMessage());
    }

    @Test
    void handleGenericAndRuntimeExceptionShouldReturnExpectedCodes() {
        var generic = handler.handleGenericException(new Exception("x"));
        var runtime = handler.handleProcessorException(new RuntimeException("bad"));

        assertEquals(500, generic.getStatusCode().value());
        assertEquals(400, runtime.getStatusCode().value());
        assertEquals("bad", runtime.getBody().getMessage());
    }
}

