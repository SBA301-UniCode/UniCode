package com.example.unicode.service;

import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PrivilegeResponse;

import java.util.List;

public interface PrivilegeService {

    PrivilegeResponse create(PrivilegeCreateRequest request);

    PrivilegeResponse getById(String privilegeCode);

    List<PrivilegeResponse> getAll();

    PrivilegeResponse update(String privilegeCode, PrivilegeUpdateRequest request);

    void delete(String privilegeCode);
}

