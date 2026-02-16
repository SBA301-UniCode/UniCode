package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.RoleResponse;
import com.example.unicode.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Role management APIs")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Role created successfully", response));
    }

    @GetMapping("/{roleCode}")
    @Operation(summary = "Get role by code")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(
            @PathVariable String roleCode) {
        RoleResponse response = roleService.getById(roleCode);
        return ResponseEntity.ok(ApiResponse.success("Role retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all roles with pagination")
    public ResponseEntity<ApiResponse<PageResponse<RoleResponse>>> getAll(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<RoleResponse> response = roleService.getAll(page, size);
        return ResponseEntity.ok(ApiResponse.success("Roles retrieved successfully", response));
    }

    @PutMapping("/{roleCode}")
    @Operation(summary = "Update role by code")
    public ResponseEntity<ApiResponse<RoleResponse>> update(
            @PathVariable String roleCode,
            @Valid @RequestBody RoleUpdateRequest request) {
        RoleResponse response = roleService.update(roleCode, request);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", response));
    }

    @DeleteMapping("/{roleCode}")
    @Operation(summary = "Delete role by code")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String roleCode) {
        roleService.delete(roleCode);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }
}

