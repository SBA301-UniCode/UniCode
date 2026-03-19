package com.example.unicode.service.impl;

import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Content;
import com.example.unicode.entity.Lesson;
import com.example.unicode.entity.Video;
import com.example.unicode.enums.ContentType;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.VideoMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.repository.VideoRepository;
import com.example.unicode.service.CloudinaryService;
import com.example.unicode.service.VideoService;
import com.example.unicode.ultils.CloudiaryUltils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final VideoMapper videoMapper;
    private final CloudiaryUltils cloudiaryUltils;
    private final CloudinaryService cloudinaryService;
    private final ContentRepo contentRepo;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    @Override
    public VideoResponse create(VideoCreateRequest request,MultipartFile file) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        List<String> cloudiary = null;
        try {
            cloudiary = cloudiaryUltils.getUrlCloudiary(file,"video_coures");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Content content = new Content();
        content.setLesson(lesson);
        content.setContentType(ContentType.VIDEO);
        content = contentRepo.save(content);
        Video video = new Video();
        video.setVideoUrl(cloudiary.get(1));
        video.setPublicId(cloudiary.get(0));
        video.setDuration(request.getDuration());
        video.setContent(content);
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
                .videoId(video.getVideoId())
                .url(signedUrl)
                .streamUrl("/api/v1/videos/" + videoId + "/stream")
                .duration(video.getDuration())
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
        video.setVideoUrl(null);
        video.setPublicId(null);
        videoRepository.save(video);
    }

    @Override
    public String getInternalVideoUrl(UUID videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_FOUND));

        String url = null;

        // Try signed URL first, fallback to stored videoUrl
        if (video.getPublicId() != null && !video.getPublicId().isEmpty()) {
            String signedUrl = cloudinaryService.generateSignedUrl(video.getPublicId());
            if (signedUrl != null && !signedUrl.isEmpty()) {
                url = signedUrl;
            }
        }
        // Fallback: use the raw URL stored in DB
        if (url == null && video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
            url = video.getVideoUrl();
        }
        if (url == null) {
            throw new AppException(ErrorCode.VIDEO_NOT_FOUND);
        }
        // Ensure URL has protocol prefix
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        log.info("Stream URL for video {}: {}", videoId, url);
        return url;
    }

}
