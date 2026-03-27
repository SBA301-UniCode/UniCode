package com.example.unicode.ultils;

import com.example.unicode.entity.Course;
import com.example.unicode.entity.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportCertificateUltilsTest {

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ExportCertificateUltils exportCertificateUltils;

    @Test
    void generateCertificateShouldCreatePdfAndUploadToS3() throws Exception {
        Course course = new Course();
        course.setTitle("Spring Boot");
        Users instructor = new Users();
        instructor.setName("Teacher A");
        course.setInstructors(instructor);

        when(s3Service.uploadPublic(any(MultipartFile.class), eq("certificate"))).thenReturn("https://s3/certificate.pdf");

        String url = exportCertificateUltils.generateCertificate("Student A", course);

        assertEquals("https://s3/certificate.pdf", url);

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(s3Service).uploadPublic(fileCaptor.capture(), eq("certificate"));
        assertEquals("certificate.pdf", fileCaptor.getValue().getOriginalFilename());
        assertEquals("application/pdf", fileCaptor.getValue().getContentType());
    }

    @Test
    void generateCertificateShouldWrapExceptions() throws Exception {
        Course course = new Course();
        course.setTitle("Spring Boot");
        Users instructor = new Users();
        instructor.setName("Teacher A");
        course.setInstructors(instructor);

        when(s3Service.uploadPublic(any(MultipartFile.class), eq("certificate")))
                .thenThrow(new IOException("S3 down"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> exportCertificateUltils.generateCertificate("Student A", course));

        assertTrue(ex.getMessage().startsWith("Lỗi export certificate"));
    }
}

