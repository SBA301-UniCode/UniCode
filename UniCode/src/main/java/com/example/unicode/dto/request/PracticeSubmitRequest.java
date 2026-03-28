package com.example.unicode.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PracticeSubmitRequest {
    private UUID submissionId;   // lần làm bài cụ thể
    private String learnerCode;  // code học viên submit
}
