package com.example.unicode.service.impl;

import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.PrivilegeResponse;
import com.example.unicode.entity.Privilege;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.PrivilegeMapper;
import com.example.unicode.repository.PrivilegeRepository;
import com.example.unicode.service.PrivilegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivilegeServiceImpl implements PrivilegeService {

    private final PrivilegeRepository privilegeRepository;
    private final PrivilegeMapper privilegeMapper;

    @Override
    public PrivilegeResponse create(PrivilegeCreateRequest request) {
        // Check if privilege already exists (including soft deleted)
        if (privilegeRepository.existsByPrivilegeCodeAndDeletedFalse(request.getPrivilegeCode())) {
            throw new AppException(ErrorCode.PRIVILEGE_ALREADY_EXISTS);
        }

        Privilege privilege = privilegeMapper.toEntity(request);
        privilege = privilegeRepository.save(privilege);

        return privilegeMapper.toResponse(privilege);
    }

    @Override
    @Transactional(readOnly = true)
    public PrivilegeResponse getById(String privilegeCode) {
        Privilege privilege = privilegeRepository.findByPrivilegeCodeAndDeletedFalse(privilegeCode)
                .orElseThrow(() -> new AppException(ErrorCode.PRIVILEGE_NOT_FOUND));

        return privilegeMapper.toResponse(privilege);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PrivilegeResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Privilege> privilegePage = privilegeRepository.findAllByDeletedFalse(pageable);

        return PageResponse.<PrivilegeResponse>builder()
                .content(privilegeMapper.toResponseList(privilegePage.getContent()))
                .currentPage(privilegePage.getNumber())
                .pageSize(privilegePage.getSize())
                .totalElements(privilegePage.getTotalElements())
                .totalPages(privilegePage.getTotalPages())
                .first(privilegePage.isFirst())
                .last(privilegePage.isLast())
                .build();
    }

    @Override
    public PrivilegeResponse update(String privilegeCode, PrivilegeUpdateRequest request) {
        Privilege privilege = privilegeRepository.findByPrivilegeCodeAndDeletedFalse(privilegeCode)
                .orElseThrow(() -> new AppException(ErrorCode.PRIVILEGE_NOT_FOUND));

        privilegeMapper.updateEntity(request, privilege);

        privilege = privilegeRepository.save(privilege);
        return privilegeMapper.toResponse(privilege);
    }

    @Override
    public void delete(String privilegeCode) {
        Privilege privilege = privilegeRepository.findByPrivilegeCodeAndDeletedFalse(privilegeCode)
                .orElseThrow(() -> new AppException(ErrorCode.PRIVILEGE_NOT_FOUND));

        // Soft delete
        privilege.setDeleted(true);
        privilege.setDeletedAt(LocalDateTime.now());
        privilege.setDeletedBy(getCurrentUser());

        privilegeRepository.save(privilege);
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}

