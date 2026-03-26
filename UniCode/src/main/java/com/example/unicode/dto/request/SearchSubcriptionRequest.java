package com.example.unicode.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.unicode.enums.StatusPayment;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SearchSubcriptionRequest {
    private UUID courseId;
    private UUID learnerId;
    private StatusPayment statusPayment;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;
}
