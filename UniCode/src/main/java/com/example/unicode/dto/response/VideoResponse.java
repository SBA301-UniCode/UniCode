package com.example.unicode.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoResponse {
    private UUID videoId;
    private String url;
    private int duration;
    private UUID contentId;
    private String publicId;

}