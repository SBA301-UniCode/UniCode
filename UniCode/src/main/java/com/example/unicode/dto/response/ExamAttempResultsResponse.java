package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempResultsResponse {
    private UUID examAttemptId;
    private String examName;
    private String learnerName;
    private double score;
    private Boolean passed;
    private LocalDateTime attemptStartTime;
    private LocalDateTime attemptEndTime;
}
