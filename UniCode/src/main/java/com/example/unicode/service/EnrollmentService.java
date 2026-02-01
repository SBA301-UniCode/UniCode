package com.example.unicode.service;

import com.example.unicode.dto.request.SearchEnrollRequest;
import com.example.unicode.dto.request.UpdateEnrollmentRequest;
import com.example.unicode.dto.response.EnrolmentResponse;
import com.example.unicode.enums.StatusCourse;
import org.springframework.data.domain.Page;
import com.example.unicode.dto.request.EnrollmentRequest;

import java.util.UUID;

public interface EnrollmentService {
    EnrolmentResponse joinCousera(UUID courseId);
    boolean isEnrolled(UUID courseId);
    Page<EnrolmentResponse> search(SearchEnrollRequest request, int page, int size);
    Page<EnrolmentResponse> getAllByCourse(UUID courseId, int page, int size);
    Page<EnrolmentResponse> getAllByLearner(UUID userId, int page, int size);
    Page<EnrolmentResponse> myLearn(StatusCourse statusCourse, int page, int size);
     EnrolmentResponse update(UpdateEnrollmentRequest request);
}
