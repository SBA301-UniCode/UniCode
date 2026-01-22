package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PrivilegeResponse;
import com.example.unicode.service.PrivilegeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/privileges")
@RequiredArgsConstructor
@Tag(name = "Privilege", description = "Privilege management APIs")
public class PrivilegeController {

    private final PrivilegeService privilegeService;

    @PostMapping
    @Operation(summary = "Create a new privilege")
    public ResponseEntity<ApiResponse<PrivilegeResponse>> create(
            @Valid @RequestBody PrivilegeCreateRequest request) {
        PrivilegeResponse response = privilegeService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Privilege created successfully", response));
    }

    @GetMapping("/{privilegeCode}")
    @Operation(summary = "Get privilege by code")
    public ResponseEntity<ApiResponse<PrivilegeResponse>> getById(
            @PathVariable String privilegeCode) {
        PrivilegeResponse response = privilegeService.getById(privilegeCode);
        return ResponseEntity.ok(ApiResponse.success("Privilege retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all privileges")
    public ResponseEntity<ApiResponse<List<PrivilegeResponse>>> getAll() {
        List<PrivilegeResponse> response = privilegeService.getAll();
        return ResponseEntity.ok(ApiResponse.success("Privileges retrieved successfully", response));
    }

    @PutMapping("/{privilegeCode}")
    @Operation(summary = "Update privilege by code")
    public ResponseEntity<ApiResponse<PrivilegeResponse>> update(
            @PathVariable String privilegeCode,
            @Valid @RequestBody PrivilegeUpdateRequest request) {
        PrivilegeResponse response = privilegeService.update(privilegeCode, request);
        return ResponseEntity.ok(ApiResponse.success("Privilege updated successfully", response));
    }

    @DeleteMapping("/{privilegeCode}")
    @Operation(summary = "Delete privilege by code")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String privilegeCode) {
        privilegeService.delete(privilegeCode);
        return ResponseEntity.ok(ApiResponse.success("Privilege deleted successfully"));
    }
}

