package com.example.unicode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoResponseUrl {
    private int duration;
    private String videoUrl;
}
