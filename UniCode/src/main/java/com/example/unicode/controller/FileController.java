package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.ultils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file, String folder) throws IOException {

        String key = s3Service.upload(file, folder);

        return ResponseEntity.ok(Map.of(
                "fileKey", key,
                "url", "https://amzn-s3-unicode.s3.ap-southeast-2.amazonaws.com/" + key
        ));
    }

    @GetMapping("/get-url")
    public ResponseEntity<?> upload(@RequestParam String key) {
        String url = s3Service.generateViewUrl(key);
        return ResponseEntity.ok(
                ApiResponse.success(url)
        );
    }
}
