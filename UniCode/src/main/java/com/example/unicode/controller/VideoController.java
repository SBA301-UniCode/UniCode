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
import com.example.unicode.ultils.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Video", description = "Video management APIs")
public class VideoController {
    private final VideoServiceImpl videoService;
    private final S3Service s3Service;
    private  final  CloudinaryService cloudinaryService;

    @PostMapping(value = "/create")
    @Operation(summary = "Create video record after client-side upload")
    public ResponseEntity<ApiResponse<VideoResponse>> create(
            @RequestPart @Valid VideoCreateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Video record created successfully",
                videoService.create(request)
        ));
    }

    // @PostMapping(value = "/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //    @Operation(summary = "Create video record after client-side upload")
    //    public ResponseEntity<ApiResponse<VideoResponse>> create(
    //            @RequestPart @Valid VideoCreateRequest request,
    //            @RequestPart MultipartFile file
    //    ) {
    //        return ResponseEntity.ok(ApiResponse.success(
    //                "Video record created successfully",
    //                videoService.create(request,file)
    //        ));
    //    }
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
    @PostMapping("/generate-upload-url")
    public ResponseEntity<?> generateUploadUrl(@RequestBody Map<String, String> request) {

        String fileName = request.get("fileName");
        String contentType = request.get("contentType");
        String size = request.get("size");

        return ResponseEntity.ok(s3Service.generateUploadUrl(fileName, contentType,Long.valueOf(size)));
    }
    @GetMapping("/video-url/{videoId}")
    public ResponseEntity<?> getVideo(@PathVariable UUID videoId) {

        return ResponseEntity.ok(videoService.getUrlToShow(videoId));
    }

// @GetMapping("/{videoId}/stream")
//    @Operation(summary = "Stream video through backend proxy (Cloudinary URL is hidden from client)")
//    public ResponseEntity<StreamingResponseBody> streamVideo(
//            @PathVariable UUID videoId,
//            @RequestHeader(value = "Range", required = false) String rangeHeader
//    ) {
//        String internalUrl = videoService.getInternalVideoUrl(videoId);
//
//        try {
//            URL url = new URL(internalUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            if (rangeHeader != null) {
//                connection.setRequestProperty("Range", rangeHeader);
//            }
//
//            int responseCode = connection.getResponseCode();
//            long contentLength = connection.getContentLengthLong();
//            String contentType = connection.getContentType();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Content-Type", contentType != null ? contentType : "video/mp4");
//            headers.set("Accept-Ranges", "bytes");
//
//            if (contentLength >= 0) {
//                headers.set("Content-Length", String.valueOf(contentLength));
//            }
//
//            String cloudContentRange = connection.getHeaderField("Content-Range");
//            if (cloudContentRange != null) {
//                headers.set("Content-Range", cloudContentRange);
//            }
//
//            HttpStatus status = (responseCode == 206) ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
//
//            StreamingResponseBody stream = outputStream -> {
//                try (InputStream inputStream = connection.getInputStream()) {
//                    byte[] buffer = new byte[8192];
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                        outputStream.flush();
//                    }
//                } catch (IOException e) {
//                    log.debug("Client disconnected during video stream: {}", e.getMessage());
//                } finally {
//                    connection.disconnect();
//                }
//            };
//
//            return ResponseEntity.status(status)
//                    .headers(headers)
//                    .body(stream);
//
//        } catch (IOException e) {
//            log.error("Failed to stream video {}: {}", videoId, e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}
