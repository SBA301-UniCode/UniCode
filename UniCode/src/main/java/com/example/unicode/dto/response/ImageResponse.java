package com.example.unicode.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageResponse {
    private String imageUrl;
    private UUID imageId;
}
