package com.example.unicode.dto.response;

import com.example.unicode.enums.StatusCourse;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EnrolmentResponse {
    private UUID enrollmentId;
    private LocalDateTime enrollmentDate;
    private StatusCourse statusCourse;
    private CourseResponse courseResponse;
}
