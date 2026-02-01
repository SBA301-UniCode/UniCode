package com.example.unicode.dto.request;

import com.example.unicode.enums.StatusCourse;
import lombok.Data;

import java.util.UUID;

@Data
public class SearchEnrollRequest {
    private String keysearch;
    private StatusCourse statusCourse;
    private UUID courseId;
    private UUID leanerId;
}
