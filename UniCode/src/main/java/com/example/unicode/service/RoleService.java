package com.example.unicode.service;

import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse create(RoleCreateRequest request);

    RoleResponse getById(String roleCode);

    List<RoleResponse> getAll();

    RoleResponse update(String roleCode, RoleUpdateRequest request);

    void delete(String roleCode);
}

