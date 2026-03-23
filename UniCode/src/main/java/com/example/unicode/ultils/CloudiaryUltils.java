package com.example.unicode.ultils;

import com.example.unicode.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for uploading files to Cloudinary.
 * Returns a List of [publicId, secureUrl] for backward compatibility.
 */
@Component
@RequiredArgsConstructor
public class CloudiaryUltils {
    private final CloudinaryService cloudinaryService;

    /**
     * Upload a file to Cloudinary and return [publicId, secureUrl].
     * @param file The MultipartFile to upload
     * @param type "image", "video", etc.
     * @return List with index 0 = publicId, index 1 = secureUrl
     */
    @SuppressWarnings("unchecked")
    public List<String> getUrlCloudiary(MultipartFile file, String type) throws IOException {
        Map<String, Object> result;
        if ("video".equalsIgnoreCase(type)) {
            result = cloudinaryService.uploadVideo(file);
        } else {
            result = cloudinaryService.uploadDocument(file);
        }
        List<String> urls = new ArrayList<>();
        urls.add((String) result.getOrDefault("public_id", ""));
        urls.add((String) result.getOrDefault("secure_url", ""));
        return urls;
    }
}
