package com.example.unicode.ultils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    @Value("${aws.s3.bucket}")
    private String bucket;
    @Value("${ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    // ================= UPLOAD DIRECT (fallback) =================
    public String uploadPublic(MultipartFile file, String folder) throws IOException {
        // 1. Tạo key duy nhất
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String key = folder + "/" + fileName;

        // 2. Tạo request upload với ACL Public Read
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ) // Yêu cầu bước 1.2 đã làm ở trên
                .build();

        // 3. Thực hiện upload
        s3Client.putObject(
                request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        // 4. Trả về URL vĩnh viễn (Sử dụng GetUrl để tự động lấy đúng Region)
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(key)).toString();
    }
    // ================= PRESIGNED UPLOAD =================
    public Map<String, String> generateUploadUrl(String fileName, String contentType, long size) {

        // 1. validate type
        List<String> allowedTypes = List.of(
                "video/mp4",
                "application/pdf",
                "image/png",
                "image/jpeg"
        );

        if (!allowedTypes.contains(contentType)) {
            throw new RuntimeException("File type not allowed");
        }

        // 2. validate extension
        if (!fileName.endsWith(".mp4") &&
                !fileName.endsWith(".pdf") &&
                !fileName.endsWith(".png") &&
                !fileName.endsWith(".jpg")) {
            throw new RuntimeException("Invalid file extension");
        }

        // 3. validate size (2GB ví dụ)
        long maxSize = 2L * 1024 * 1024 * 1024;
        if (size > maxSize) {
            throw new RuntimeException("File too large");
        }

        // 4. folder
        String folder = getFolderByType(contentType);
        String key = folder + "/raw/" + UUID.randomUUID() + "-" + fileName;

        // 5. tạo presigned PUT
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(p -> p
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(putObjectRequest)
                );

        Map<String, String> result = new HashMap<>();
        result.put("uploadUrl", presignedRequest.url().toString());
        result.put("key", key);

        return result;
    }

    // ================= VIEW URL =================
    public String generateViewUrl(String key,int duration) {

        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket) // ✅ FIX
                .key(key)
                .build();

        PresignedGetObjectRequest presigned =
                s3Presigner.presignGetObject(r -> r
                        .signatureDuration(Duration.ofMinutes(duration + 2))
                        .getObjectRequest(getReq));

        return presigned.url().toString();
    }
    public String convertToHlsFast(String inputKey) throws Exception {
        // 1️⃣ Tạo thư mục temp
        String videoId = UUID.randomUUID().toString();
        String localDir = System.getProperty("java.io.tmpdir") + "/" + videoId;
        File dir = new File(localDir);
        if (!dir.exists()) dir.mkdirs();

        // 2️⃣ Stream MP4 từ S3
        String s3Url = generateViewUrl(inputKey, 10); // pre-signed 10 phút

        // 3️⃣ FFmpeg command - multi-threaded + HLS segment
        String segmentPattern = localDir + "/seg_%03d.ts";
        List<String> cmd = new ArrayList<>();
        cmd.add(ffmpegPath);
        cmd.add("-y");
        cmd.add("-i");
        cmd.add(s3Url);
        cmd.addAll(List.of(
                "-c:v", "libx264",
                "-preset", "fast",
                "-threads", "0",         // tận dụng tất cả CPU core
                "-b:v", "4M",
                "-c:a", "aac",
                "-b:a", "128k",
                "-hls_time", "6",
                "-hls_list_size", "0",
                "-hls_segment_filename", segmentPattern,
                localDir + "/index.m3u8"
        ));

        // 4️⃣ Chạy FFmpeg
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO(); // để xem log
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new RuntimeException("FFmpeg failed with code " + exitCode);

        // 5️⃣ Upload song song bằng S3 Multipart + Executor
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) throw new RuntimeException("No HLS files generated");

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 luồng upload
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (File f : files) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    String key = "videos/hls/" + videoId + "/" + f.getName();

                    // Dùng putObject bình thường, AWS SDK tự chia multipart nếu > 5MB
                    s3Client.putObject(
                            PutObjectRequest.builder()
                                    .bucket(bucket)
                                    .key(key)
                                    .contentType(getContentType(f.getName()))
                                    .build(),
                            RequestBody.fromFile(f)
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // 6️⃣ Cleanup local temp
        for (File f : files) f.delete();
        dir.delete();

        // 7️⃣ Trả về link HLS
        return "videos/hls/" + videoId + "/index.m3u8";
    }
    // ================= HELPERS =================
    private String getFolderByType(String contentType) {
        if (contentType.startsWith("video/")) return "videos";
        if (contentType.startsWith("image/")) return "images";
        if (contentType.equals("application/pdf")) return "documents";
        return "others";
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".m3u8")) return "application/vnd.apple.mpegurl";
        if (filename.endsWith(".ts")) return "video/MP2T";
        return "application/octet-stream";
    }
    public void deleteFile(String key) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}