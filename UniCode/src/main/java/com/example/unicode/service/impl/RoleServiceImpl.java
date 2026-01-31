package com.example.unicode.service.impl;

import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.RoleResponse;
import com.example.unicode.entity.Privilege;
import com.example.unicode.entity.Role;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.RoleMapper;
import com.example.unicode.repository.PrivilegeRepo;
import com.example.unicode.repository.RoleRepo;
import com.example.unicode.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;
    private final PrivilegeRepo privilegeRepo;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse create(RoleCreateRequest request) {
        // Check if role already exists
        if (roleRepo.existsByRoleCodeAndDeletedFalse(request.getRoleCode())) {
            throw new AppException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        Role role = roleMapper.toEntity(request);

        // Set privileges if provided
        if (request.getPrivilegeCodes() != null && !request.getPrivilegeCodes().isEmpty()) {
            Set<Privilege> privileges = new HashSet<>();
            for (String privilegeCode : request.getPrivilegeCodes()) {
                Privilege privilege = privilegeRepo.findByPrivilegeCodeAndDeletedFalse(privilegeCode)
                        .orElseThrow(() -> new AppException(ErrorCode.PRIVILEGE_NOT_FOUND));
                privileges.add(privilege);
            }
            role.setPrivileges(privileges);
        }

        role = roleRepo.save(role);
        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(String roleCode) {
        Role role = roleRepo.findByRoleCodeAndDeletedFalse(roleCode)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        return roleMapper.toResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoleResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage = roleRepo.findAllByDeletedFalse(pageable);

        return PageResponse.<RoleResponse>builder()
                .content(roleMapper.toResponseList(rolePage.getContent()))
                .currentPage(rolePage.getNumber())
                .pageSize(rolePage.getSize())
                .totalElements(rolePage.getTotalElements())
                .totalPages(rolePage.getTotalPages())
                .first(rolePage.isFirst())
                .last(rolePage.isLast())
                .build();
    }

    @Override
    public RoleResponse update(String roleCode, RoleUpdateRequest request) {
        Role role = roleRepo.findByRoleCodeAndDeletedFalse(roleCode)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        roleMapper.updateEntity(request, role);

        // Update privileges if provided
        if (request.getPrivilegeCodes() != null) {
            Set<Privilege> privileges = new HashSet<>();
            for (String privilegeCode : request.getPrivilegeCodes()) {
                Privilege privilege = privilegeRepo.findByPrivilegeCodeAndDeletedFalse(privilegeCode)
                        .orElseThrow(() -> new AppException(ErrorCode.PRIVILEGE_NOT_FOUND));
                privileges.add(privilege);
            }
            role.setPrivileges(privileges);
        }

        role = roleRepo.save(role);
        return roleMapper.toResponse(role);
    }

    @Override
    public void delete(String roleCode) {
        Role role = roleRepo.findByRoleCodeAndDeletedFalse(roleCode)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        // Soft delete
        role.setDeleted(true);
        role.setDeletedAt(LocalDateTime.now());
        role.setDeletedBy(getCurrentUser());

        roleRepo.save(role);
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}

