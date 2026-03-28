package com.example.unicode.service.impl;

import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PrivilegeResponse;
import com.example.unicode.entity.Privilege;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.PrivilegeMapper;
import com.example.unicode.repository.PrivilegeRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivilegeServiceImplTest {

    @Mock
    private PrivilegeRepository privilegeRepository;
    @Mock
    private PrivilegeMapper privilegeMapper;

    @InjectMocks
    private PrivilegeServiceImpl privilegeService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createShouldThrowWhenPrivilegeCodeExists() {
        PrivilegeCreateRequest request = new PrivilegeCreateRequest("COURSE_READ", "Read", "desc");
        when(privilegeRepository.existsByPrivilegeCodeAndDeletedFalse("COURSE_READ")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> privilegeService.create(request));

        assertEquals(ErrorCode.PRIVILEGE_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void createShouldPersistAndReturnResponse() {
        PrivilegeCreateRequest request = new PrivilegeCreateRequest("COURSE_READ", "Read", "desc");
        Privilege entity = new Privilege();
        Privilege saved = new Privilege();
        PrivilegeResponse response = new PrivilegeResponse();
        response.setPrivilegeCode("COURSE_READ");

        when(privilegeRepository.existsByPrivilegeCodeAndDeletedFalse("COURSE_READ")).thenReturn(false);
        when(privilegeMapper.toEntity(request)).thenReturn(entity);
        when(privilegeRepository.save(entity)).thenReturn(saved);
        when(privilegeMapper.toResponse(saved)).thenReturn(response);

        PrivilegeResponse result = privilegeService.create(request);

        assertEquals("COURSE_READ", result.getPrivilegeCode());
    }

    @Test
    void updateShouldThrowWhenPrivilegeNotFound() {
        PrivilegeUpdateRequest request = new PrivilegeUpdateRequest("Read", "new desc");
        when(privilegeRepository.findByPrivilegeCodeAndDeletedFalse("MISSING")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> privilegeService.update("MISSING", request));

        assertEquals(ErrorCode.PRIVILEGE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteShouldSoftDeleteWithAuditor() {
        Privilege privilege = new Privilege();
        when(privilegeRepository.findByPrivilegeCodeAndDeletedFalse("COURSE_READ")).thenReturn(Optional.of(privilege));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        privilegeService.delete("COURSE_READ");

        ArgumentCaptor<Privilege> captor = ArgumentCaptor.forClass(Privilege.class);
        verify(privilegeRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
        assertNotNull(captor.getValue().getDeletedAt());
    }
}
