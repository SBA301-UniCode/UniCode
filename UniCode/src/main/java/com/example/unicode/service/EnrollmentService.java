package com.example.unicode.service;

import com.example.unicode.dto.request.EnrollmentRequest;

import java.util.UUID;

public interface EnrollmentService {

    void enrollUserToCourse(EnrollmentRequest enrollmentRequest, UUID userId);
}
