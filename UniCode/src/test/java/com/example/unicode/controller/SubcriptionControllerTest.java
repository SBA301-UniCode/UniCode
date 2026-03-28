package com.example.unicode.controller;

import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.service.SubcriptionService;
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
class SubcriptionControllerTest {

    @Mock
    private SubcriptionService subcriptionService;

    @InjectMocks
    private SubcriptionController controller;

    @Test
    void getByIdShouldDelegate() {
        UUID id = UUID.randomUUID();
        when(subcriptionService.getById(id)).thenReturn(new SubcriptionResponse());

        var response = controller.getById(id);

        assertEquals(200, response.getStatusCode().value());
        verify(subcriptionService).getById(id);
    }
}

