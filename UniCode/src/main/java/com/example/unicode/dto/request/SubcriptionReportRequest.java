package com.example.unicode.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SubcriptionReportRequest {
    private LocalDate from;
    private  LocalDate to;
}
