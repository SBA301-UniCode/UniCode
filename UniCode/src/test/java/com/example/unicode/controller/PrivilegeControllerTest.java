package com.example.unicode.controller;

import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.PrivilegeResponse;
import com.example.unicode.service.PrivilegeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrivilegeControllerTest {

    @Mock
    private PrivilegeService privilegeService;

    @InjectMocks
    private PrivilegeController controller;

    @Test
    void createShouldReturnCreated() {
        when(privilegeService.create(null)).thenReturn(new PrivilegeResponse());

        var response = controller.create(null);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Privilege created successfully", response.getBody().getMessage());
        verify(privilegeService).create(null);
    }

    @Test
    void getByIdShouldDelegate() {
        when(privilegeService.getById("P_READ")).thenReturn(new PrivilegeResponse());

        var response = controller.getById("P_READ");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Privilege retrieved successfully", response.getBody().getMessage());
        verify(privilegeService).getById("P_READ");
    }

    @Test
    void getAllShouldDelegate() {
        PageResponse<PrivilegeResponse> page = PageResponse.<PrivilegeResponse>builder()
                .content(List.of(new PrivilegeResponse()))
                .build();
        when(privilegeService.getAll(0, 10)).thenReturn(page);

        var response = controller.getAll(0, 10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().getContent().size());
        verify(privilegeService).getAll(0, 10);
    }

    @Test
    void updateShouldDelegate() {
        when(privilegeService.update("P_READ", null)).thenReturn(new PrivilegeResponse());

        var response = controller.update("P_READ", null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Privilege updated successfully", response.getBody().getMessage());
        verify(privilegeService).update("P_READ", null);
    }

    @Test
    void deleteShouldDelegate() {
        var response = controller.delete("P_READ");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Privilege deleted successfully", response.getBody().getMessage());
        verify(privilegeService).delete("P_READ");
    }
}
