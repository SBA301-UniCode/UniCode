package com.example.unicode.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class EnrollmentReportResponse {
    private  UserResponse userResponse;
    private  double percentComplete;
    private UUID enrollmentId;
    private CourseResponse courseResponse;
}
