package com.example.unicode.service.impl;

import com.example.unicode.dto.request.VideoCreateRequest;
import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.dto.response.VideoResponseUrl;
import com.example.unicode.entity.*;
import com.example.unicode.enums.VideoStatus;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.VideoMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.CloudinaryService;
import com.example.unicode.ultils.S3Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private VideoMapper videoMapper;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private ContentRepo contentRepo;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private UsersRepository usersRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    private UUID lessonId;
    private UUID videoId;
    private UUID contentId;
    private Lesson lesson;
    private Content content;
    private Video video;
    private Users user;
    private Course course;

    @BeforeEach
    void setUp() {
        lessonId = UUID.randomUUID();
        videoId = UUID.randomUUID();
        contentId = UUID.randomUUID();

        course = new Course();
        course.setCourseId(UUID.randomUUID());

        Chapter chapter = new Chapter();
        chapter.setChapterId(UUID.randomUUID());
        chapter.setCourse(course);

        lesson = new Lesson();
        lesson.setLessonId(lessonId);
        lesson.setChapter(chapter);

        content = new Content();
        content.setContentId(contentId);
        content.setLesson(lesson);

        video = new Video();
        video.setVideoId(videoId);
        video.setContent(content);
        video.setDuration(120);
        video.setPublicId("pub_id");

        user = new Users();
        user.setUserId(UUID.randomUUID());
        user.setEmail("test@test.com");

        setupSecurityContext("test@test.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(String username) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);
    }

    // --- create ---

    @Test
    void create_ShouldThrowException_WhenLessonNotFound() {
        VideoCreateRequest request = new VideoCreateRequest();
        request.setLessonId(lessonId);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> videoService.create(request));
        assertEquals("Lesson not found", ex.getMessage());
    }

    @Test
    void create_ShouldCreateVideo_AndProcessSync() throws Exception {
        VideoCreateRequest request = new VideoCreateRequest();
        request.setLessonId(lessonId);
        request.setDuration(100);
        request.setKey("s3_key");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(s3Service.convertToHlsFast("s3_key")).thenReturn("hls_key");
        when(videoRepository.save(any(Video.class))).thenAnswer(i -> i.getArgument(0));

        VideoResponse expectedResponse = new VideoResponse();
        when(videoMapper.toResponse(any(Video.class))).thenReturn(expectedResponse);

        VideoResponse result = videoService.create(request);

        assertNotNull(result);
        verify(contentRepo).save(any(Content.class));
        verify(s3Service).convertToHlsFast("s3_key");
        verify(videoRepository, atLeastOnce()).save(any(Video.class));
    }

    @Test
    void create_ShouldCatchProcessVideoException() throws Exception {
        VideoCreateRequest request = new VideoCreateRequest();
        request.setLessonId(lessonId);
        request.setKey("s3_key");

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(s3Service.convertToHlsFast("s3_key")).thenThrow(new RuntimeException("S3 error"));
        when(videoRepository.save(any(Video.class))).thenAnswer(i -> i.getArgument(0));
        when(videoMapper.toResponse(any(Video.class))).thenReturn(new VideoResponse());

        VideoResponse result = videoService.create(request);

        assertNotNull(result);
        verify(videoRepository, atLeastOnce()).save(argThat(v -> v.getStatus() == VideoStatus.READY));
    }

    // --- getAllActiveVideos ---

    @Test
    void getAllActiveVideos_ShouldReturnList() {
        when(videoRepository.findAllByDeletedFalse()).thenReturn(List.of(video));
        when(videoMapper.toResponseList(anyList())).thenReturn(List.of(new VideoResponse()));

        List<VideoResponse> result = videoService.getAllActiveVideos();

        assertFalse(result.isEmpty());
    }

    // --- getVideoDetail ---

    @Test
    void getVideoDetail_ShouldThrowException_WhenNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> videoService.getVideoDetail(videoId));
        assertEquals("Video not found", ex.getMessage());
    }

    @Test
    void getVideoDetail_ShouldReturnSignedUrl() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(cloudinaryService.generateSignedUrl("pub_id")).thenReturn("signed_url");

        VideoResponse result = videoService.getVideoDetail(videoId);

        assertEquals("signed_url", result.getUrl());
    }

    // --- delete ---

    @Test
    void delete_ShouldThrowAppException_WhenVideoNotFound() {
        when(videoRepository.findByContent_ContentId(contentId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> videoService.delete(contentId));
        assertEquals(ErrorCode.VIDEO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void delete_ShouldDeleteVideoAndCloudinary() throws Exception {
        when(videoRepository.findByContent_ContentId(contentId)).thenReturn(Optional.of(video));

        videoService.delete(contentId);

        verify(cloudinaryService).deleteVideo("pub_id");
        assertTrue(video.getDeleted());
        assertNotNull(video.getDeletedAt());
        verify(videoRepository).save(video);
    }

    @Test
    void delete_ShouldNotDeleteCloudinary_IfPublicIdNull() throws Exception {
        video.setPublicId(null);
        when(videoRepository.findByContent_ContentId(contentId)).thenReturn(Optional.of(video));

        videoService.delete(contentId);

        verify(cloudinaryService, never()).deleteVideo(anyString());
        assertTrue(video.getDeleted());
        verify(videoRepository).save(video);
    }

    // --- uploadChunk ---

    @Test
    void uploadChunk_ShouldReturnNull_WhenNoSecureUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());
        when(cloudinaryService.uploadChunk(any(byte[].class), eq("uid"), eq(0L), eq(100L)))
                .thenReturn(Collections.emptyMap());

        VideoResponse result = videoService.uploadChunk(lessonId, file, "uid", 0, 100);

        assertNull(result);
    }

    @Test
    void uploadChunk_ShouldCreateVideo_WhenSecureUrlExists() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());
        Map<String, Object> map = new HashMap<>();
        map.put("secure_url", "url");
        map.put("public_id", "pub");
        map.put("duration", 10.5);
        when(cloudinaryService.uploadChunk(any(byte[].class), eq("uid"), eq(0L), eq(100L)))
                .thenReturn(map);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(videoRepository.save(any(Video.class))).thenAnswer(i -> i.getArgument(0));
        when(videoMapper.toResponse(any(Video.class))).thenReturn(new VideoResponse());

        VideoResponse result = videoService.uploadChunk(lessonId, file, "uid", 0, 100);

        assertNotNull(result);
        verify(videoRepository).save(argThat(v -> v.getDuration() == 10 && "pub".equals(v.getPublicId())));
    }

    @Test
    void uploadChunk_ShouldDefaultDurationZero_WhenDurationMissing() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());
        Map<String, Object> map = new HashMap<>();
        map.put("secure_url", "url");
        map.put("public_id", "pub_without_duration");
        when(cloudinaryService.uploadChunk(any(byte[].class), eq("uid"), eq(0L), eq(100L))).thenReturn(map);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(videoRepository.save(any(Video.class))).thenAnswer(i -> i.getArgument(0));
        when(videoMapper.toResponse(any(Video.class))).thenReturn(new VideoResponse());

        VideoResponse result = videoService.uploadChunk(lessonId, file, "uid", 0, 100);

        assertNotNull(result);
        verify(videoRepository).save(argThat(v -> v.getDuration() == 0 && "pub_without_duration".equals(v.getPublicId())));
    }

    @Test
    void uploadChunk_ShouldThrowException_WhenLessonNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());
        Map<String, Object> map = new HashMap<>();
        map.put("secure_url", "url");
        map.put("public_id", "pub");
        when(cloudinaryService.uploadChunk(any(byte[].class), eq("uid"), eq(0L), eq(100L)))
                .thenReturn(map);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> videoService.uploadChunk(lessonId, file, "uid", 0, 100));
        assertEquals("Lesson not found", ex.getMessage());
    }

    // --- uploadLocalChunk ---

    @Test
    void uploadLocalChunk_ShouldReturnNull_WhenNotAllChunksReceived() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "chunkdata1".getBytes());
        String uid = "test_upload_id_not_full_" + UUID.randomUUID();

        VideoResponse result = videoService.uploadLocalChunk(lessonId, file, uid, 0, 2);

        assertNull(result);

        String tmpDir = System.getProperty("java.io.tmpdir") + "/unicode_uploads/";
        File chunk = new File(tmpDir + uid + "/chunk_0");
        if (chunk.exists()) {
            assertTrue(chunk.delete());
        }
        File dir = new File(tmpDir + uid);
        if (dir.exists()) {
            assertTrue(dir.delete());
        }
    }

    @Test
    void uploadLocalChunk_ShouldMergeAndStartAsync_WhenAllChunksReceived() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("file", "test.mp4", "video/mp4", "chunkdata1".getBytes());
        String uid = "test_upload_id_full_" + UUID.randomUUID();

        String tmpDir = System.getProperty("java.io.tmpdir") + "/unicode_uploads/";
        File dir = new File(tmpDir + uid);
        if (!dir.exists()) {
            assertTrue(dir.mkdirs());
        }
        Files.write(new File(dir, "chunk_0").toPath(), "chunkdata0".getBytes());

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);

        Video saved = new Video();
        saved.setVideoId(videoId);
        when(videoRepository.save(any(Video.class))).thenReturn(saved);
        when(videoMapper.toResponse(saved)).thenReturn(new VideoResponse());

        VideoResponse result = videoService.uploadLocalChunk(lessonId, file1, uid, 1, 2);

        assertNotNull(result);
    }

    @Test
    void uploadLocalChunk_ShouldUpdateVideo_WhenAsyncUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "single_chunk".getBytes());
        String uid = "test_upload_id_async_success_" + UUID.randomUUID();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(videoMapper.toResponse(any(Video.class))).thenReturn(new VideoResponse());

        Video savedVideo = new Video();
        savedVideo.setVideoId(videoId);
        savedVideo.setPublicId("processing_" + uid);
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video v = invocation.getArgument(0);
            if (v.getVideoId() == null) {
                v.setVideoId(videoId);
            }
            savedVideo.setVideoId(v.getVideoId());
            savedVideo.setPublicId(v.getPublicId());
            savedVideo.setDuration(v.getDuration());
            return v;
        });
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(savedVideo));

        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("public_id", "cloud_public_id");
        uploadResult.put("secure_url", "https://cdn/url");
        uploadResult.put("duration", 22.7);
        when(cloudinaryService.uploadVideoFile(any(File.class))).thenReturn(uploadResult);

        VideoResponse result = videoService.uploadLocalChunk(lessonId, file, uid, 0, 1);
        assertNotNull(result);

        Thread.sleep(3600);

        assertEquals("cloud_public_id", savedVideo.getPublicId());
        assertEquals(22, savedVideo.getDuration());
        verify(videoRepository, atLeast(2)).save(any(Video.class));
    }

    @Test
    void uploadLocalChunk_ShouldMarkFailed_WhenAsyncUploadThrows() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "single_chunk_fail".getBytes());
        String uid = "test_upload_id_async_fail_" + UUID.randomUUID();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(contentRepo.save(any(Content.class))).thenReturn(content);
        when(videoMapper.toResponse(any(Video.class))).thenReturn(new VideoResponse());

        Video savedVideo = new Video();
        savedVideo.setVideoId(videoId);
        savedVideo.setPublicId("processing_" + uid);
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video v = invocation.getArgument(0);
            if (v.getVideoId() == null) {
                v.setVideoId(videoId);
            }
            savedVideo.setVideoId(v.getVideoId());
            savedVideo.setPublicId(v.getPublicId());
            savedVideo.setDuration(v.getDuration());
            return v;
        });
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(savedVideo));
        when(cloudinaryService.uploadVideoFile(any(File.class))).thenThrow(new RuntimeException("cloudinary down"));

        VideoResponse result = videoService.uploadLocalChunk(lessonId, file, uid, 0, 1);
        assertNotNull(result);

        Thread.sleep(3600);

        assertEquals("FAILED_" + uid, savedVideo.getPublicId());
        verify(videoRepository, atLeast(2)).save(any(Video.class));
    }

    @Test
    void uploadLocalChunk_ShouldThrowException_WhenLessonNotFound() {
        MockMultipartFile file = new MockMultipartFile("file", "test.mp4", "video/mp4", "data".getBytes());
        String uid = "test_upload_id_error_" + UUID.randomUUID();

        File dir = new File(System.getProperty("java.io.tmpdir") + "/unicode_uploads/" + uid);
        if (!dir.exists()) {
            assertTrue(dir.mkdirs());
        }
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> videoService.uploadLocalChunk(lessonId, file, uid, 0, 1));
        assertEquals("Lesson not found", ex.getMessage());
    }

    // --- getUrlToShow ---

    @Test
    void getUrlToShow_ShouldThrowAppException_WhenVideoNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> videoService.getUrlToShow(videoId));
        assertEquals(ErrorCode.VIDEO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getUrlToShow_ShouldThrowAppException_WhenUserNotFound() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(usersRepository.findByEmail("test@test.com")).thenReturn(null);

        AppException ex = assertThrows(AppException.class, () -> videoService.getUrlToShow(videoId));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getUrlToShow_ShouldThrowAppException_WhenNoPermission() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(usersRepository.findByEmail("test@test.com")).thenReturn(user);
        when(enrollmentRepository.existsByCourseAndLearner(course, user)).thenReturn(false);
        when(courseRepository.existsByInstructors(user)).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> videoService.getUrlToShow(videoId));
        assertEquals(ErrorCode.NOT_HAVE_PERMISION_TO_VIEW, ex.getErrorCode());
    }

    @Test
    void getUrlToShow_ShouldReturnUrl_WhenEnrolled() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(usersRepository.findByEmail("test@test.com")).thenReturn(user);
        when(enrollmentRepository.existsByCourseAndLearner(course, user)).thenReturn(true);
        when(s3Service.generateViewUrl("pub_id", 120)).thenReturn("s3_url");

        VideoResponseUrl result = videoService.getUrlToShow(videoId);

        assertEquals("s3_url", result.getVideoUrl());
        assertEquals(120, result.getDuration());
    }

    @Test
    void getUrlToShow_ShouldReturnUrl_WhenInstructor() {
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(usersRepository.findByEmail("test@test.com")).thenReturn(user);
        when(enrollmentRepository.existsByCourseAndLearner(course, user)).thenReturn(false);
        when(courseRepository.existsByInstructors(user)).thenReturn(true);
        when(s3Service.generateViewUrl("pub_id", 120)).thenReturn("s3_url");

        VideoResponseUrl result = videoService.getUrlToShow(videoId);

        assertEquals("s3_url", result.getVideoUrl());
    }

    // --- processVideo ---

    @Test
    void processVideo_ShouldSetReadyAndPublicId_WhenConvertSuccess() throws Exception {
        Video toProcess = new Video();
        toProcess.setStatus(VideoStatus.IN_PROCESSING);

        when(s3Service.convertToHlsFast("input_key")).thenReturn("hls_key");

        videoService.processVideo(toProcess, "input_key");

        assertEquals(VideoStatus.READY, toProcess.getStatus());
        assertEquals("hls_key", toProcess.getPublicId());
        verify(videoRepository).save(toProcess);
    }

    @Test
    void processVideo_ShouldSetFailed_WhenConvertThrowsException() throws Exception {
        Video toProcess = new Video();
        toProcess.setStatus(VideoStatus.IN_PROCESSING);

        when(s3Service.convertToHlsFast("input_key")).thenThrow(new Exception("convert failed"));

        videoService.processVideo(toProcess, "input_key");

        assertEquals(VideoStatus.FAILED, toProcess.getStatus());
        verify(videoRepository).save(toProcess);
    }
}
