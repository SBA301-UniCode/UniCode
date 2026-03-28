package com.example.unicode.service.impl;

import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.CertificateMapper;
import com.example.unicode.repository.CertificateRepository;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.ProcessService;
import com.example.unicode.ultils.ExportCertificateUltils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CertificateMapper certificateMapper;
    @Mock
    private ProcessService processService;
    @Mock
    private ExportCertificateUltils exportCertificateUltils;

    @InjectMocks
    private CertificateServiceImpl certificateService;

    @Test
    void getBySerialNumberShouldThrowWhenNotFound() {
        when(certificateRepository.findBySerialNumberAndDeletedFalse("SERIAL")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> certificateService.getBySerialNumber("SERIAL"));

        assertEquals(ErrorCode.CERTIFICATE_NOT_FOUND, ex.getErrorCode());
    }
}

