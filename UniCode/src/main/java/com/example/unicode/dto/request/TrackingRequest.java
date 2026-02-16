package com.example.unicode.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TrackingRequest {
    private UUID id;
    private UUID enrollmentId;
}
