package com.example.unicode.controller;

import com.example.unicode.dto.request.LoginRequest;
import com.example.unicode.service.AuthencationSevice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthencationControllerTest {

    @Mock
    private AuthencationSevice authencationSevice;

    @InjectMocks
    private AuthencationController controller;

    @Test
    void loginShouldDelegateToService() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user@test.com");
        when(authencationSevice.login(request)).thenReturn(null);

        var response = controller.login(request);

        assertTrue(response.isSuccess());
        verify(authencationSevice).login(request);
    }
}

