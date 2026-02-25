package com.example.unicode.service.impl;

import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import com.example.unicode.entity.Video;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.VideoMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.EnrollmentRepository;
import com.example.unicode.repository.VideoRepository;
import com.example.unicode.service.CloudinaryService;
import com.example.unicode.service.VideoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final ContentRepo contentRepo;
    private final VideoMapper videoMapper;
    private final CloudinaryService cloudinaryService;
    private final EnrollmentRepository enrollmentRepository;


    @Override
    @Transactional
    public VideoResponse create(VideoCreateRequest request, MultipartFile file) throws IOException {
        var content = contentRepo.findById(request.getContentId())
                .orElseThrow(() -> new AppException(ErrorCode.CONTENT_NOT_FOUND));
        var uploadResult = cloudinaryService.uploadVideo(file);
        String secureUrl = (String) uploadResult.get("secure_url");
        String publicId = (String) uploadResult.get("public_id");

        Video video = Video.builder()
                .videoUrl(secureUrl)
                .publicId(publicId)
                .duration(request.getDuration())
                .content(content)
                .build();
        video.setDeleted(false);

        video = videoRepository.save(video);
        return videoMapper.toResponse(video);
    }

    @Override
    public List<VideoResponse> getAllActiveVideos() {
        List<Video> videos = videoRepository.findAllByDeletedFalse();
        return videoMapper.toResponseList(videos);
    }

    @Override
    public VideoResponse getVideoDetails(UUID contentId) throws AccessDeniedException {
        Video video = videoRepository.findByContent_ContentId(contentId)
                .orElseThrow(() -> new AppException(ErrorCode.VIDEO_NOT_FOUND));
        return videoMapper.toResponse(video);
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
