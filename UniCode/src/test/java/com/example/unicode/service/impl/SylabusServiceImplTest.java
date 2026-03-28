package com.example.unicode.service.impl;

import com.example.unicode.dto.request.SylabusCreateRequest;
import com.example.unicode.entity.Sylabus;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.SylabusMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.SylabusRepository;
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
class SylabusServiceImplTest {

    @Mock
    private SylabusRepository sylabusRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private SylabusMapper sylabusMapper;

    @InjectMocks
    private SylabusServiceImpl sylabusService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getByIdShouldThrowWhenSylabusNotFound() {
        when(sylabusRepository.findBySylabusIdAndDeletedFalse("S1")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> sylabusService.getById("S1"));

        assertEquals(ErrorCode.SYLLABUS_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void deleteShouldSoftDeleteSylabus() {
        Sylabus sylabus = new Sylabus();
        when(sylabusRepository.findBySylabusIdAndDeletedFalse("S1")).thenReturn(Optional.of(sylabus));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@test.com", "pwd", AuthorityUtils.createAuthorityList("ROLE_ADMIN"))
        );

        sylabusService.delete("S1");

        ArgumentCaptor<Sylabus> captor = ArgumentCaptor.forClass(Sylabus.class);
        verify(sylabusRepository).save(captor.capture());
        assertTrue(captor.getValue().getDeleted());
        assertEquals("admin@test.com", captor.getValue().getDeletedBy());
    }
}

