package com.example.unicode.service;

import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.PrivilegeResponse;

public interface PrivilegeService {

    PrivilegeResponse create(PrivilegeCreateRequest request);

    PrivilegeResponse getById(String privilegeCode);

    PageResponse<PrivilegeResponse> getAll(int page, int size);

    PrivilegeResponse update(String privilegeCode, PrivilegeUpdateRequest request);

    void delete(String privilegeCode);
}

