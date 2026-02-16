package com.example.unicode.service;

import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Users;
import com.example.unicode.entity.Video;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

public interface VideoService {
    VideoResponse create(VideoCreateRequest request, MultipartFile file) throws IOException;
    List<VideoResponse> getAllActiveVideos();
    VideoResponse getVideoDetails(UUID contentId) throws AccessDeniedException;
    @Transactional
    void delete(UUID contentId) throws IOException;
}
