package com.example.unicode.service;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Users;

import java.util.UUID;

public interface UserService {

    UserResponse create(UserCreateRequest request);

    UserResponse getById(UUID userId);

    UserResponse getByEmail(String email);

    PageResponse<UserResponse> getAll(int page, int size,boolean deleted);

    UserResponse update(UUID userId, UserUpdateRequest request);

    void modifiUser(UUID userId,boolean delete);

    UserResponse getMyInfo();

    Users getUsers();
}

