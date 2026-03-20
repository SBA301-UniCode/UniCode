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
import com.example.unicode.ultils.CloudiaryUltils;
import com.example.unicode.ultils.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final S3Service s3Service;
    private final UsersRepository usersRepository;
    private final CourseRepository courseRepository;

//    @Transactional
//    @Override
//    public VideoResponse create(VideoCreateRequest request,MultipartFile file) {
//        Lesson lesson = lessonRepository.findById(request.getLessonId())
//                .orElseThrow(() -> new RuntimeException("Lesson not found"));
//
//        List<String> cloudiary = null;
//        try {
//            cloudiary = cloudiaryUltils.getUrlCloudiary(file,"video_coures");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Content content = new Content();
//        content.setLesson(lesson);
//        content.setContentType(ContentType.VIDEO);
//        content = contentRepo.save(content);
//        Video video = new Video();
//        video.setVideoUrl(cloudiary.get(1));
//        video.setPublicId(cloudiary.get(0));
//        video.setDuration(request.getDuration());
//        video.setContent(content);
//        return videoMapper.toResponse(videoRepository.save(video));
//    }

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
        video.setStatus(VideoStatus.IN_PROCESSING);
        video.setContent(content);
        video.setDuration(request.getDuration());
//        video.setPublicId(request.getKey());
        processVideo(video,request.getKey());
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
// @Override
//    public VideoResponse getVideoDetail(UUID videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new RuntimeException("Video not found"));
//
//        return VideoResponse.builder()
//                .videoId(video.getVideoId())
//                .streamUrl("/api/v1/videos/" + videoId + "/stream")
//                .duration(video.getDuration())
//                .build();
//    }

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
// @Override
//    public String getInternalVideoUrl(UUID videoId) {
//        Video video = videoRepository.findById(videoId)
//                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_FOUND));
//
//        String url = null;
//
//        // Try signed URL first, fallback to stored videoUrl
//        if (video.getPublicId() != null && !video.getPublicId().isEmpty()) {
//            String signedUrl = cloudinaryService.generateSignedUrl(video.getPublicId());
//            if (signedUrl != null && !signedUrl.isEmpty()) {
//                url = signedUrl;
//            }
//        }
//        // Fallback: use the raw URL stored in DB
//        if (url == null && video.getVideoUrl() != null && !video.getVideoUrl().isEmpty()) {
//            url = video.getVideoUrl();
//        }
//        if (url == null) {
//            throw new AppException(ErrorCode.VIDEO_NOT_FOUND);
//        }
//        // Ensure URL has protocol prefix
//        if (!url.startsWith("http://") && !url.startsWith("https://")) {
//            url = "https://" + url;
//        }
//        log.info("Stream URL for video {}: {}", videoId, url);
//        return url;
//    }

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

        if(!enrollmentRepository.existsByCourseAndLearner(course, users) && !courseRepository.existsByInstructors(users)){
            throw  new AppException(ErrorCode.NOT_HAVE_PERMISION_TO_VIEW);
        }


        String url = s3Service.generateViewUrl(video.getPublicId(),  video.getDuration());

        return new VideoResponseUrl(video.getDuration(),url);
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
