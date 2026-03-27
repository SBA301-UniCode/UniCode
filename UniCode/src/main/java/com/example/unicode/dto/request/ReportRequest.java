package com.example.unicode.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ReportRequest {
    @NotNull(message = "FromDate is not null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;
    @NotNull(message ="ToDate is not null")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private  LocalDate to;
}
