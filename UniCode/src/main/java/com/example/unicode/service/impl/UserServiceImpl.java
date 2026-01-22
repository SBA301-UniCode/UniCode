package com.example.unicode.service.impl;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Role;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.UserMapper;
import com.example.unicode.repository.RoleRepo;
import com.example.unicode.repository.UsersRepo;
import com.example.unicode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UsersRepo usersRepo;
    private final RoleRepo roleRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // Check if email already exists
        if (usersRepo.existsByEmailAndDeletedFalse(request.getEmail())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Users user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set roles if provided, otherwise set default LEARNER role
        if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleCode : request.getRoleCodes()) {
                Role role = roleRepo.findByRoleCodeAndDeletedFalse(roleCode)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
            user.setRolesList(roles);
        } else {
            Role defaultRole = roleRepo.findByRoleCodeAndDeletedFalse("LEARNER")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.setRolesList(Set.of(defaultRole));
        }

        user = usersRepo.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID userId) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        Users user = usersRepo.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(usersRepo.findAllByDeletedFalse());
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateEntity(request, user);

        // Update roles if provided
        if (request.getRoleCodes() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleCode : request.getRoleCodes()) {
                Role role = roleRepo.findByRoleCodeAndDeletedFalse(roleCode)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
            user.setRolesList(roles);
        }

        user = usersRepo.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void delete(UUID userId) {
        Users user = usersRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Soft delete
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(getCurrentUser());

        usersRepo.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepo.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}

