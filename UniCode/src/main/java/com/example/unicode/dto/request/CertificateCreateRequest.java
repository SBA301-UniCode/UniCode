package com.example.unicode.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateCreateRequest {

    @NotNull(message = "Learner ID is required")
    private UUID learnerId;

    @NotNull(message = "Course ID is required")
    private UUID courseId;
}

