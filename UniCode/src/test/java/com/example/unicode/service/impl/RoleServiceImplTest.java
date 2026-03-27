package com.example.unicode.service.impl;

import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.RoleResponse;
import com.example.unicode.entity.Privilege;
import com.example.unicode.entity.Role;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.RoleMapper;
import com.example.unicode.repository.PrivilegeRepository;
import com.example.unicode.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PrivilegeRepository privilegeRepository;
    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldThrowWhenRoleExists() {
        RoleCreateRequest request = new RoleCreateRequest("ADMIN", "Admin", "desc", Set.of());
        when(roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> roleService.create(request));

        assertEquals(ErrorCode.ROLE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void createShouldThrowWhenPrivilegeNotFound() {
        RoleCreateRequest request = new RoleCreateRequest("ADMIN", "Admin", "desc", Set.of("P1"));
        Role role = new Role();

        when(roleRepository.existsByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(false);
        when(roleMapper.toEntity(request)).thenReturn(role);
        when(privilegeRepository.findByPrivilegeCodeAndDeletedFalse("P1")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> roleService.create(request));

        assertEquals(ErrorCode.PRIVILEGE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void updateShouldApplyPrivilegesAndReturnResponse() {
        RoleUpdateRequest request = new RoleUpdateRequest("Admin", "updated", Set.of("P1"));
        Role role = new Role();
        Privilege privilege = new Privilege("P1", "P1", "desc");
        RoleResponse response = new RoleResponse();
        response.setRoleCode("ADMIN");

        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.of(role));
        when(privilegeRepository.findByPrivilegeCodeAndDeletedFalse("P1")).thenReturn(Optional.of(privilege));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toResponse(role)).thenReturn(response);

        RoleResponse result = roleService.update("ADMIN", request);

        assertEquals("ADMIN", result.getRoleCode());
        assertEquals(1, role.getPrivileges().size());
    }

    @Test
    void deleteShouldSoftDeleteRole() {
        Role role = new Role();
        when(roleRepository.findByRoleCodeAndDeletedFalse("ADMIN")).thenReturn(Optional.of(role));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        roleService.delete("ADMIN");

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
    }
}
