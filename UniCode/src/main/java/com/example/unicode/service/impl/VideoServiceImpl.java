package com.example.unicode.service.impl;

import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.dto.response.VideoResponseUrl;
import com.example.unicode.entity.*;
import com.example.unicode.enums.ContentType;
import com.example.unicode.enums.VideoStatus;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.VideoMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.CloudinaryService;
import com.example.unicode.service.VideoService;
import com.example.unicode.ultils.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final VideoMapper videoMapper;
    private final CloudinaryService cloudinaryService;
    private final ContentRepo contentRepo;
    private final EnrollmentRepository enrollmentRepository;
    private final S3Service s3Service;
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;

    @Transactional
    @Override
    public VideoResponse create(VideoCreateRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Content content = new Content();
        content.setLesson(lesson);
        content.setContentType(ContentType.VIDEO);
        content = contentRepo.save(content);

        Video video = new Video();
        video.setDuration(request.getDuration());
        video.setStatus(VideoStatus.IN_PROCESSING);
        video.setContent(content);
        processVideo(video, request.getKey());
        video.setStatus(VideoStatus.READY);
        return videoMapper.toResponse(videoRepository.save(video));
    }

    @Override
    public List<VideoResponse> getAllActiveVideos() {
        List<Video> videos = videoRepository.findAllByDeletedFalse();
        return videoMapper.toResponseList(videos);
    }

    @Override
    public VideoResponse getVideoDetail(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        String publicId = video.getPublicId();
        String signedUrl = cloudinaryService.generateSignedUrl(publicId);

        return VideoResponse.builder()
                .url(signedUrl)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID contentId) throws IOException {
        Video video = videoRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_FOUND));

        if (video.getPublicId() != null) {
            cloudinaryService.deleteVideo(video.getPublicId());
        }
        video.setDeleted(true);
        video.setDeletedAt(LocalDateTime.now());
        videoRepository.save(video);
    }

    @Override
    public VideoResponse uploadChunk(UUID lessonId, MultipartFile file, String uploadId, long startByte, long totalSize)
            throws IOException {
        Map<String, Object> uploadResult = cloudinaryService.uploadChunk(file.getBytes(), uploadId, startByte,
                totalSize);

        if (uploadResult.containsKey("secure_url")) {
            String secureUrl = (String) uploadResult.get("secure_url");
            String publicId = (String) uploadResult.get("public_id");
            int duration = 0;
            if (uploadResult.containsKey("duration")) {
                duration = ((Number) uploadResult.get("duration")).intValue();
            }

            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

            Content content = new Content();
            content.setLesson(lesson);
            content.setContentType(ContentType.VIDEO);
            content = contentRepo.save(content);

            Video video = new Video();
            video.setPublicId(publicId);
            video.setDuration(duration);
            video.setContent(content);
            return videoMapper.toResponse(videoRepository.save(video));
        }
        return null;
    }

    @Override
    public VideoResponse uploadLocalChunk(UUID lessonId, MultipartFile file, String uploadId, int chunkIndex, int totalChunks) throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir") + "/unicode_uploads/";
        java.io.File dir = new java.io.File(tmpDir + uploadId);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        java.io.File chunkFile = new java.io.File(dir, "chunk_" + chunkIndex);
        file.transferTo(chunkFile);

        boolean isReadyToMerge = false;

        // Block đồng bộ hóa nhiều luồng ghép file (tránh 2 luồng cùng merge 1 lúc)
        synchronized (uploadId.intern()) {
            java.io.File[] chunks = dir.listFiles();
            // Nếu đủ mảnh và chưa có file mp4 hợp nhất
            if (chunks != null && chunks.length == totalChunks && !new java.io.File(tmpDir + uploadId + ".mp4").exists()) {
                isReadyToMerge = true;
                // Tạo sẵn file trống để luồng khác không nhảy vào nữa
                new java.io.File(tmpDir + uploadId + ".mp4").createNewFile();
            }
        }

        if (isReadyToMerge) {
            // merge chunks
            java.io.File mergedFile = new java.io.File(tmpDir + uploadId + ".mp4");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(mergedFile)) {
                for (int i = 0; i < totalChunks; i++) {
                    java.io.File c = new java.io.File(dir, "chunk_" + i);
                    java.nio.file.Files.copy(c.toPath(), fos);
                }
            }

            // Cleanup chunk files
            java.io.File[] chunks = dir.listFiles();
            if (chunks != null) {
                for (java.io.File c : chunks) {
                    c.delete();
                }
            }
            dir.delete();

            // Create placeholder record
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

            Content content = new Content();
            content.setLesson(lesson);
            content.setContentType(ContentType.VIDEO);
            content = contentRepo.save(content);

            Video video = new Video();
            video.setPublicId("processing_" + uploadId);
            video.setDuration(0);
            video.setContent(content);
            Video savedVideo = videoRepository.save(video);
            UUID videoId = savedVideo.getVideoId();

            // Start async upload thread
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(3000);
                    System.out.println("Bắt đầu upload background file " + mergedFile.getName() + " lên Cloudinary...");
                    Map<String, Object> uploadResult = cloudinaryService.uploadVideoFile(mergedFile);

                    String publicId = (String) uploadResult.get("public_id");
                    String secureUrl = (String) uploadResult.get("secure_url");
                    int duration = 0;
                    if (uploadResult.containsKey("duration")) {
                        duration = ((Number) uploadResult.get("duration")).intValue();
                    }

                    Video v = videoRepository.findById(videoId).orElse(null);
                    if (v != null) {
                        v.setPublicId(publicId);
                        v.setDuration(duration);
                        videoRepository.save(v);
                        System.out.println("Upload hoàn tất cho video: " + videoId);
                    } else {
                        System.err.println("Lỗi logic trầm trọng: Không tìm thấy Video ID " + videoId);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi upload background for " + mergedFile.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    Video v = videoRepository.findById(videoId).orElse(null);
                    if (v != null) {
                        v.setPublicId("FAILED_" + uploadId);
                        videoRepository.save(v);
                    }
                } finally {
                    mergedFile.delete();
                }
            });

            return videoMapper.toResponse(savedVideo);
        }

        return null;
    }

    @Override
    public VideoResponseUrl getUrlToShow(UUID uuid) {
        Video video = videoRepository.findById(uuid).orElseThrow(
                () -> new AppException(ErrorCode.VIDEO_NOT_FOUND)
        );
        Course course = video.getContent().getLesson().getChapter().getCourse();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepository.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        if (!enrollmentRepository.existsByCourseAndLearner(course, users) && !courseRepository.existsByInstructors(users)) {
            throw new AppException(ErrorCode.NOT_HAVE_PERMISION_TO_VIEW);
        }

        String url = s3Service.generateViewUrl(video.getPublicId(), video.getDuration());
        return new VideoResponseUrl(video.getDuration(), url);
    }

    @Async
    public void processVideo(Video video, String key) {
        try {
            String hlsKey = s3Service.convertToHlsFast(key);
            video.setPublicId(hlsKey);
            video.setStatus(VideoStatus.READY);
            videoRepository.save(video);
        } catch (Exception e) {
            e.printStackTrace();
            video.setStatus(VideoStatus.FAILED);
            videoRepository.save(video);
        }
    }
}
