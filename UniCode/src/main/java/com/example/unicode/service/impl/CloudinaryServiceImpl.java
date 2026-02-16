package com.example.unicode.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.unicode.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;
    @Override
    public Map uploadVideo(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "video",
                        "folder", "video_courses",
                        "type", "authenticated"
                ));
    }
    @Override
    public String generateSignedUrl(String publicId) {
        long now = System.currentTimeMillis() / 1000L;
        // Cộng thêm 120 giây (2 phút)
        long expirationTime = now + 120;
        return cloudinary.url()
                .resourceType("video")
                .type("authenticated")
                .signed(true)
                .generate(publicId + "?expires_at=" + expirationTime);
    }

    @Override
    public void deleteVideo(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
    }
}