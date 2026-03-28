package com.example.unicode.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class EnrollementBannerRequest {
    private UUID userId;
    private UUID coureId;
}
