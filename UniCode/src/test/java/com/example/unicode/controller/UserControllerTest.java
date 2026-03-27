package com.example.unicode.controller;

import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.service.EnrollmentService;
import com.example.unicode.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private UserController controller;

    @Test
    void getByIdShouldDelegate() {
        UUID userId = UUID.randomUUID();
        when(userService.getById(userId)).thenReturn(new UserResponse());

        var response = controller.getById(userId);

        assertEquals(200, response.getStatusCode().value());
        verify(userService).getById(userId);
    }
}

