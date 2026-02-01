package com.example.unicode.dto.request;

import com.example.unicode.enums.StatusCourse;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateEnrollmentRequest {
    private UUID enrollmentId;
    private StatusCourse statusCourse;
}
