package com.example.unicode.controller;

import com.example.unicode.base.ApiResponse;
import com.example.unicode.dto.request.ContentCreateRequest;
import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.ContentResponse;
import com.example.unicode.dto.response.PageResponse;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Users;
import com.example.unicode.repository.UsersRepository;
import com.example.unicode.service.CloudinaryService;
import com.example.unicode.service.UserService;
import com.example.unicode.service.impl.VideoServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Tag(name = "Video", description = "Video management APIs")
public class VideoController {
    private final VideoServiceImpl videoService;
    private final UserService userService;
    private final UsersRepository usersRepository;
    private final CloudinaryService cloudinaryService;



    @PostMapping("/create")
    @Operation(summary = "Create video record after client-side upload")
    public ResponseEntity<ApiResponse<VideoResponse>> create(
            @RequestBody @Valid VideoCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Video record created successfully",
                videoService.create(request)
        ));
    }
    @GetMapping
    @Operation(summary = "Get all list videos")
    public  ResponseEntity<ApiResponse<List<VideoResponse>>> getAcctiveVideo(){
        List<VideoResponse> response = videoService.getAllActiveVideos();
        return ResponseEntity.ok(ApiResponse.success("Get list active video successfully", response));
    }

    @DeleteMapping("/{contentId}")
    @Operation(summary = "Delete Video by ID (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable UUID contentId) throws IOException {
        videoService.delete(contentId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Video deleted successfully")
                .build());
    }

    @GetMapping("/{videoId}")
    @Operation(summary = "Get detail video")
    public ResponseEntity<ApiResponse<VideoResponse>> getVideoDetail(@PathVariable UUID videoId) {
        VideoResponse response = videoService.getVideoDetail(videoId);
        return ResponseEntity.ok(ApiResponse.success("Get video detail successfully", response));
    }


    @GetMapping("/upload-signature")
    @Operation(summary = "Get signature for direct client-side upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadSignature() {
        return ResponseEntity.ok(ApiResponse.success(
                "Signature generated successfully",
                cloudinaryService.getUploadSignature()
        ));
    }



}
