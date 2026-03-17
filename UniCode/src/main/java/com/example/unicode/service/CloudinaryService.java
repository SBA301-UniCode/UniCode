package com.example.unicode.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map uploadVideo(MultipartFile file) throws IOException;
    Map uploadDocument(MultipartFile file) throws IOException;
    String generateSignedUrl(String publicId);
    String generateSignedDocumentUrl(String publicId, String resourceType);
    void deleteVideo(String publicId) throws IOException;

    Map<String, Object> getUploadSignature();
}