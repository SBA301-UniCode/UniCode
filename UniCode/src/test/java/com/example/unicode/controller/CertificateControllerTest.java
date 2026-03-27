package com.example.unicode.controller;

import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.service.CertificateService;
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
class CertificateControllerTest {

    @Mock
    private CertificateService certificateService;

    @InjectMocks
    private CertificateController controller;

    @Test
    void getByIdShouldReturnOk() {
        UUID id = UUID.randomUUID();
        when(certificateService.getById(id)).thenReturn(new CertificateResponse());

        var response = controller.getById(id);

        assertEquals(200, response.getStatusCode().value());
        verify(certificateService).getById(id);
    }
}

