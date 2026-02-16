package com.example.unicode.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map uploadVideo(MultipartFile file) throws IOException;
    String generateSignedUrl(String publicId);
    void deleteVideo(String publicId) throws IOException;
}