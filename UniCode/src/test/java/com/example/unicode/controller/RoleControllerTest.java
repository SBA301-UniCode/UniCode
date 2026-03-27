package com.example.unicode.controller;

import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.RoleResponse;
import com.example.unicode.service.RoleService;
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
class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController controller;

    @Test
    void createShouldReturnCreated() {
        when(roleService.create(null)).thenReturn(new RoleResponse());

        var response = controller.create(null);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Role created successfully", response.getBody().getMessage());
        verify(roleService).create(null);
    }

    @Test
    void getByIdShouldDelegate() {
        when(roleService.getById("ADMIN")).thenReturn(new RoleResponse());

        var response = controller.getById("ADMIN");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role retrieved successfully", response.getBody().getMessage());
        verify(roleService).getById("ADMIN");
    }

    @Test
    void getAllShouldDelegate() {
        PageResponse<RoleResponse> page = PageResponse.<RoleResponse>builder()
                .content(List.of(new RoleResponse()))
                .build();
        when(roleService.getAll(1, 5)).thenReturn(page);

        var response = controller.getAll(1, 5);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getData().getContent().size());
        verify(roleService).getAll(1, 5);
    }

    @Test
    void updateShouldDelegate() {
        when(roleService.update("ADMIN", null)).thenReturn(new RoleResponse());

        var response = controller.update("ADMIN", null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role updated successfully", response.getBody().getMessage());
        verify(roleService).update("ADMIN", null);
    }

    @Test
    void deleteShouldDelegate() {
        var response = controller.delete("ADMIN");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Role deleted successfully", response.getBody().getMessage());
        verify(roleService).delete("ADMIN");
    }
}
