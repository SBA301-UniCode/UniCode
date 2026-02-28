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
import com.example.unicode.service.ContentService;
import com.example.unicode.service.VideoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LessonRepository lessonRepository;
    private final VideoMapper videoMapper;
    private final CloudinaryService cloudinaryService;
    private final ContentRepo contentRepo;
    private final EnrollmentRepository enrollmentRepository;


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
        video.setVideoUrl(request.getUrl());
        video.setPublicId(request.getPublicId());
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
                .url(signedUrl)
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






}
