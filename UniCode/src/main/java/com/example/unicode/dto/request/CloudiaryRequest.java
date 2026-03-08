package com.example.unicode.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CloudiaryRequest {
    private MultipartFile file;
    private String api_key;
    private long timestamp;
    private String signature;
    private String folder;
    private String type;
}
