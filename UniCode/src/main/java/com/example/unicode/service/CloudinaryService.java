package com.example.unicode.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map uploadVideo(MultipartFile file) throws IOException;

    @SuppressWarnings("unchecked")
    Map<String, Object> uploadVideoFile(java.io.File file) throws IOException;

    Map uploadDocument(MultipartFile file) throws IOException;

    String generateSignedUrl(String publicId);
    void deleteVideo(String publicId) throws IOException;

    String generateSignedDocumentUrl(String publicId, String resourceType);

    Map<String, Object> getUploadSignature();

    Map<String, Object> uploadChunk(byte[] chunkData, String uploadId, long startByte, long totalSize) throws IOException;
}