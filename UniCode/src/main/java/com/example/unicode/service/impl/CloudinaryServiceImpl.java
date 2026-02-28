package com.example.unicode.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.transformation.Layer;
import com.cloudinary.utils.ObjectUtils;
import com.example.unicode.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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
                        "type", "upload"
                ));
    }
    @Override
    public String generateSignedUrl(String publicId) {
        return cloudinary.url()
                .resourceType("video")
                .type("upload")
                .transformation(new Transformation()
                        .overlay(new Layer().publicId("logo_folder/my_logo")) // Public ID của file Logo đã up
                        .gravity("north_east") // Đặt ở góc trên bên phải
                        .x(20).y(20)           // Cách lề 20px
                        .width(150)            // Độ rộng của logo
                        .opacity(50)           // Làm mờ 50% để không che nội dung bài giảng
                )
                .generate(publicId);
    }

    @Override
    public void deleteVideo(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
    }

    @Override
    public Map<String, Object> getUploadSignature() {
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> params = new java.util.TreeMap<>();
        params.put("folder", "video_courses");
        params.put("timestamp", timestamp);
        params.put("type", "upload");

        System.out.println("--- DEBUG SIGNATURE ---");
        System.out.println("Params to sign: " + params);

        String signature = cloudinary.apiSignRequest(params, cloudinary.config.apiSecret);
        System.out.println("Generated Signature: " + signature);
        System.out.println("-----------------------");

        Map<String, Object> response = new HashMap<>(params);
        response.put("signature", signature);
        response.put("api_key", cloudinary.config.apiKey);
        response.put("cloud_name", cloudinary.config.cloudName);
        return response;
    }



}