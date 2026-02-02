package com.example.unicode.dto.request;

import com.example.unicode.enums.StatusContent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCreateRequest {

    @NotNull(message = "EnrollmentId is required")
    private UUID enrollmentId;
    @NotNull(message = "CourseId is required")
    private UUID courseId;
}
