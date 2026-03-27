package com.example.unicode.service.impl;

import com.example.unicode.configuration.MomoConfig;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.SubcriptionMapper;
import com.example.unicode.mapper.SumariesMapper;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.SubcriptionRepository;
import com.example.unicode.repository.SumariesRepository;
import com.example.unicode.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubcriptionServiceImplTest {

    @Mock
    private SubcriptionRepository subcriptionRepository;
    @Mock
    private SubcriptionMapper subcriptionMapper;
    @Mock
    private MomoConfig config;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private SumariesRepository sumariesRepository;
    @Mock
    private SumariesMapper sumariesMapper;

    @InjectMocks
    private SubcriptionServiceImpl subcriptionService;

    @Test
    void getByIdShouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(subcriptionRepository.findById(id)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> subcriptionService.getById(id));

        assertEquals(ErrorCode.SUBCRIPTION_NOT_FOUND, ex.getErrorCode());
    }
}

