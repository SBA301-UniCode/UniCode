package com.example.unicode.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InstructorReport {
    private long numberOfCourse;
    private long totalStudent;
    private double revenua;
    List<ReportSumaries> reports;

    @Builder
    @Data
    public static class ReportSumaries
    {
        private LocalDate localDate;
        private double price;
    }
}
