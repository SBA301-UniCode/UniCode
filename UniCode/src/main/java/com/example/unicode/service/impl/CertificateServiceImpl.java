package com.example.unicode.service.impl;

import com.example.unicode.dto.request.CertificateCreateRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.entity.Certificate;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.CertificateMapper;
import com.example.unicode.repository.CertificateRepo;
import com.example.unicode.repository.CourseRepo;
import com.example.unicode.repository.UsersRepo;
import com.example.unicode.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepo certificateRepo;
    private final UsersRepo usersRepo;
    private final CourseRepo courseRepo;
    private final CertificateMapper certificateMapper;

    @Override
    public CertificateResponse create(CertificateCreateRequest request) {
        // Check if user exists
        Users learner = usersRepo.findByUserIdAndDeletedFalse(request.getLearnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if course exists
        Course course = courseRepo.findByCourseIdAndDeletedFalse(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Check if certificate already exists for this user and course
        if (certificateRepo.existsByLearner_UserIdAndCourse_CourseIdAndDeletedFalse(
                request.getLearnerId(), request.getCourseId())) {
            throw new AppException(ErrorCode.CERTIFICATE_ALREADY_EXISTS);
        }

        Certificate certificate = new Certificate();
        certificate.setLearner(learner);
        certificate.setCourse(course);
        certificate.setCertificateDate(LocalDateTime.now());

        certificate = certificateRepo.save(certificate);
        return certificateMapper.toResponse(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getById(UUID certificateId) {
        Certificate certificate = certificateRepo.findByCertificateIdAndDeletedFalse(certificateId)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        return certificateMapper.toResponse(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CertificateResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Certificate> certificatePage = certificateRepo.findAllByDeletedFalse(pageable);

        return PageResponse.<CertificateResponse>builder()
                .content(certificateMapper.toResponseList(certificatePage.getContent()))
                .currentPage(certificatePage.getNumber())
                .pageSize(certificatePage.getSize())
                .totalElements(certificatePage.getTotalElements())
                .totalPages(certificatePage.getTotalPages())
                .first(certificatePage.isFirst())
                .last(certificatePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateResponse> getByLearnerId(UUID learnerId) {
        // Check if user exists
        if (!usersRepo.existsByUserIdAndDeletedFalse(learnerId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        return certificateMapper.toResponseList(
                certificateRepo.findAllByLearner_UserIdAndDeletedFalse(learnerId));
    }

    @Override
    public void delete(UUID certificateId) {
        Certificate certificate = certificateRepo.findByCertificateIdAndDeletedFalse(certificateId)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        // Soft delete
        certificate.setDeleted(true);
        certificate.setDeletedAt(LocalDateTime.now());
        certificate.setDeletedBy(getCurrentUser());

        certificateRepo.save(certificate);
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}

