package com.example.unicode.service;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse getById(UUID userId);

    UserResponse getByEmail(String email);

    List<UserResponse> getAll();

    UserResponse update(UUID userId, UserUpdateRequest request);

    void delete(UUID userId);

    UserResponse getMyInfo();
}

