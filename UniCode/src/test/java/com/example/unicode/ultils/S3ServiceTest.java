package com.example.unicode.ultils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Utilities;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        s3Service = new S3Service(s3Client, s3Presigner);
        ReflectionTestUtils.setField(s3Service, "bucket", "unicode-bucket");
        ReflectionTestUtils.setField(s3Service, "ffmpegPath", "missing-ffmpeg-binary");
    }

    @Test
    void generateUploadUrlShouldReturnUploadUrlAndKey() throws Exception {
        PresignedPutObjectRequest presignedPut = mock(PresignedPutObjectRequest.class);
        when(presignedPut.url()).thenReturn(new URL("https://example.com/upload"));
        when(s3Presigner.presignPutObject(org.mockito.ArgumentMatchers.<Consumer<PutObjectPresignRequest.Builder>>any()))
                .thenReturn(presignedPut);

        Map<String, String> result = s3Service.generateUploadUrl("video.mp4", "video/mp4", 1024);

        assertEquals("https://example.com/upload", result.get("uploadUrl"));
        assertTrue(result.get("key").startsWith("videos/raw/"));
        assertTrue(result.get("key").endsWith("-video.mp4"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<PutObjectPresignRequest.Builder>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(s3Presigner).presignPutObject(captor.capture());

        PutObjectPresignRequest.Builder builder = PutObjectPresignRequest.builder();
        captor.getValue().accept(builder);
        PutObjectPresignRequest request = builder.build();

        assertEquals(Duration.ofMinutes(10), request.signatureDuration());
        assertEquals("unicode-bucket", request.putObjectRequest().bucket());
        assertEquals("video/mp4", request.putObjectRequest().contentType());
        assertEquals(result.get("key"), request.putObjectRequest().key());
    }

    @Test
    void generateUploadUrlShouldMapFoldersByType() throws Exception {
        PresignedPutObjectRequest presignedPut = mock(PresignedPutObjectRequest.class);
        when(presignedPut.url()).thenReturn(new URL("https://example.com/upload"));
        when(s3Presigner.presignPutObject(org.mockito.ArgumentMatchers.<Consumer<PutObjectPresignRequest.Builder>>any()))
                .thenReturn(presignedPut);

        Map<String, String> imageResult = s3Service.generateUploadUrl("avatar.jpg", "image/jpeg", 5);
        Map<String, String> pdfResult = s3Service.generateUploadUrl("doc.pdf", "application/pdf", 5);

        assertTrue(imageResult.get("key").startsWith("images/raw/"));
        assertTrue(pdfResult.get("key").startsWith("documents/raw/"));
    }

    @Test
    void generateUploadUrlShouldValidateInput() {
        RuntimeException badType = assertThrows(RuntimeException.class,
                () -> s3Service.generateUploadUrl("video.mp4", "text/plain", 1));
        assertEquals("File type not allowed", badType.getMessage());

        RuntimeException badExt = assertThrows(RuntimeException.class,
                () -> s3Service.generateUploadUrl("video.txt", "video/mp4", 1));
        assertEquals("Invalid file extension", badExt.getMessage());

        RuntimeException tooLarge = assertThrows(RuntimeException.class,
                () -> s3Service.generateUploadUrl("video.mp4", "video/mp4", 3L * 1024 * 1024 * 1024));
        assertEquals("File too large", tooLarge.getMessage());
    }

    @Test
    void generateUploadUrlShouldAcceptMaxBoundarySize() throws Exception {
        PresignedPutObjectRequest presignedPut = mock(PresignedPutObjectRequest.class);
        when(presignedPut.url()).thenReturn(new URL("https://example.com/upload"));
        when(s3Presigner.presignPutObject(org.mockito.ArgumentMatchers.<Consumer<PutObjectPresignRequest.Builder>>any()))
                .thenReturn(presignedPut);

        long maxSize = 2L * 1024 * 1024 * 1024;
        Map<String, String> result = s3Service.generateUploadUrl("video.mp4", "video/mp4", maxSize);

        assertEquals("https://example.com/upload", result.get("uploadUrl"));
    }

    @Test
    void generateViewUrlShouldUsePresigner() throws Exception {
        PresignedGetObjectRequest presignedGet = mock(PresignedGetObjectRequest.class);
        when(presignedGet.url()).thenReturn(new URL("https://example.com/view"));
        when(s3Presigner.presignGetObject(org.mockito.ArgumentMatchers.<Consumer<GetObjectPresignRequest.Builder>>any()))
                .thenReturn(presignedGet);

        String url = s3Service.generateViewUrl("videos/raw/a.mp4", 10);

        assertEquals("https://example.com/view", url);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<GetObjectPresignRequest.Builder>> captor = ArgumentCaptor.forClass(Consumer.class);
        verify(s3Presigner).presignGetObject(captor.capture());

        GetObjectPresignRequest.Builder builder = GetObjectPresignRequest.builder();
        captor.getValue().accept(builder);
        GetObjectPresignRequest request = builder.build();
        GetObjectRequest getObjectRequest = request.getObjectRequest();

        assertEquals(Duration.ofMinutes(12), request.signatureDuration());
        assertEquals("unicode-bucket", getObjectRequest.bucket());
        assertEquals("videos/raw/a.mp4", getObjectRequest.key());
    }

    @Test
    void uploadPublicShouldUploadWithPublicAclAndReturnS3Url() throws Exception {
        S3Utilities utilities = mock(S3Utilities.class);
        when(s3Client.utilities()).thenReturn(utilities);
        when(utilities.getUrl(any(Consumer.class))).thenReturn(new URL("https://example.com/public-file"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "video.mp4",
                "video/mp4",
                "hello".getBytes()
        );

        String result = s3Service.uploadPublic(file, "videos");

        assertEquals("https://example.com/public-file", result);

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals("unicode-bucket", request.bucket());
        assertTrue(request.key().startsWith("videos/"));
        assertTrue(request.key().endsWith("-video.mp4"));
        assertEquals("video/mp4", request.contentType());
        assertEquals(ObjectCannedACL.PUBLIC_READ, request.acl());
    }

    @Test
    void deleteFileShouldCallS3DeleteObject() {
        s3Service.deleteFile("videos/raw/a.mp4");

        ArgumentCaptor<DeleteObjectRequest> captor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(captor.capture());
        assertEquals("unicode-bucket", captor.getValue().bucket());
        assertEquals("videos/raw/a.mp4", captor.getValue().key());
    }

    @Test
    void convertToHlsFastShouldFailFastWhenFfmpegCommandIsInvalid() {
        assertThrows(Exception.class, () -> s3Service.convertToHlsFast("videos/raw/source.mp4"));
    }
}
