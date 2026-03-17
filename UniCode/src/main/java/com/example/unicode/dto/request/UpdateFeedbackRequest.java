package com.example.unicode.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateFeedbackRequest {
    private String comment;
    private int rating;
    private List<UUID> imageRemoveId;
}
