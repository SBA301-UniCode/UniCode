package com.example.unicode.service;

import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.RoleResponse;

public interface RoleService {

    RoleResponse create(RoleCreateRequest request);

    RoleResponse getById(String roleCode);

    PageResponse<RoleResponse> getAll(int page, int size);

    RoleResponse update(String roleCode, RoleUpdateRequest request);

    void delete(String roleCode);
}

