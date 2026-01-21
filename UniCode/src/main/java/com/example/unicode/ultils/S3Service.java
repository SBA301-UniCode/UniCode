package com.example.unicode.ultils;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String folder) throws IOException {

        String key = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(request,
                RequestBody.fromBytes(file.getBytes()));
        return key; // lưu key vào DB
    }
    public String generateViewUrl(String key) {

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket("amzn-s3-unicode")
                .key(key)
                .build();
        PresignedGetObjectRequest presigned =
                s3Presigner.presignGetObject(r -> r
                        .signatureDuration(Duration.ofMinutes(5))
                        .getObjectRequest(getReq));
        return presigned.url().toString();
    }


}
