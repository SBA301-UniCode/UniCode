package com.example.unicode.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class FeedbackRequest {
    private String comment;
    private int rating;
}
