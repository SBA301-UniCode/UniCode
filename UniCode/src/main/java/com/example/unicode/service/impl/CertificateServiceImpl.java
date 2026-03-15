package com.example.unicode.service.impl;

import com.example.unicode.dto.request.CertificateCreateRequest;
import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.response.CertificateResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.entity.Certificate;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Enrollment;
import com.example.unicode.entity.Users;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.CertificateMapper;
import com.example.unicode.repository.CertificateRepository;
import com.example.unicode.repository.CourseRepository;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.CertificateService;
import com.example.unicode.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CertificateMapper certificateMapper;
    private final ProcessService processService;

    @Override
    public CertificateResponse create(CertificateCreateRequest request) {
        // Check if user exists
        Users learner = usersRepository.findByUserIdAndDeletedFalse(request.getLearnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Check if course exists
        Course course = courseRepository.findByCourseIdAndDeletedFalse(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // Check if certificate already exists for this user and course
        if (certificateRepository.existsByLearner_UserIdAndCourse_CourseIdAndDeletedFalse(
                request.getLearnerId(), request.getCourseId())) {
            throw new AppException(ErrorCode.CERTIFICATE_ALREADY_EXISTS);
        }

        // Validate course completion: find enrollment and check progress
        List<Enrollment> enrollments = enrollmentRepository
                .findAllByLearner_UserIdAndCourse_CourseIdAndDeletedFalse(
                        request.getLearnerId(), request.getCourseId());

        if (enrollments.isEmpty()) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // Check progress for each enrollment to find one with 100% completion
        boolean courseCompleted = false;
        for (Enrollment enrollment : enrollments) {
            try {
                TrackingResponse progress = processService.getProcessOfCourses(
                        TrackingRequest.builder()
                                .id(request.getCourseId())
                                .enrollmentId(enrollment.getEnrollmentId())
                                .build());
                if (progress != null && progress.getPercentComplete() >= 100) {
                    courseCompleted = true;
                    break;
                }
            } catch (Exception e) {
                // If progress check fails for this enrollment, try next
            }
        }

        if (!courseCompleted) {
            throw new AppException(ErrorCode.COURSE_NOT_COMPLETED);
        }

        // Generate serial number
        String serialNumber = generateSerialNumber();

        Certificate certificate = new Certificate();
        certificate.setLearner(learner);
        certificate.setCourse(course);
        certificate.setCertificateDate(LocalDateTime.now());
        certificate.setSerialNumber(serialNumber);

        certificate = certificateRepository.save(certificate);
        return certificateMapper.toResponse(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getById(UUID certificateId) {
        Certificate certificate = certificateRepository.findByCertificateIdAndDeletedFalse(certificateId)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        return certificateMapper.toResponse(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CertificateResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Certificate> certificatePage = certificateRepository.findAllByDeletedFalse(pageable);

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
        if (!usersRepository.existsByUserIdAndDeletedFalse(learnerId)) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        return certificateMapper.toResponseList(
                certificateRepository.findAllByLearner_UserIdAndDeletedFalse(learnerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificateResponse> getMyList() {
        String currentUserEmail = getCurrentUser();
        Users user = usersRepository.findByEmailAndDeletedFalse(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return certificateMapper.toResponseList(
                certificateRepository.findAllByLearner_UserIdAndDeletedFalse(user.getUserId()));
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateResponse getBySerialNumber(String serialNumber) {
        Certificate certificate = certificateRepository.findBySerialNumberAndDeletedFalse(serialNumber)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        return certificateMapper.toResponse(certificate);
    }

    @Override
    public void delete(UUID certificateId) {
        Certificate certificate = certificateRepository.findByCertificateIdAndDeletedFalse(certificateId)
                .orElseThrow(() -> new AppException(ErrorCode.CERTIFICATE_NOT_FOUND));

        certificate.setDeleted(true);
        certificate.setDeletedAt(LocalDateTime.now());
        certificate.setDeletedBy(getCurrentUser());

        certificateRepository.save(certificate);
    }

    private String generateSerialNumber() {
        String year = String.valueOf(Year.now().getValue());
        String randomPart;
        String serialNumber;
        do {
            randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            serialNumber = "UC-" + year + "-" + randomPart;
        } while (certificateRepository.findBySerialNumberAndDeletedFalse(serialNumber).isPresent());
        return serialNumber;
    }

    private String getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM";
        }
        return authentication.getName();
    }
}
