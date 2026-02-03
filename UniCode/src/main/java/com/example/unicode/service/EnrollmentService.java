package com.example.unicode.service;

import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.dto.request.EnrollmentRequest;

import java.util.UUID;

public interface EnrollmentService {
    EnrolmentResponse joinCousera(UUID courseId);
    boolean isEnrolled(UUID courseId);

}
