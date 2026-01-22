package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateResponse {

    private UUID certificateId;

    private LocalDateTime certificateDate;

    private UUID learnerId;

    private String learnerName;

    private String learnerEmail;

    private UUID courseId;

    private String courseTitle;

    private LocalDateTime createdAt;
}

