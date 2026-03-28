package com.example.unicode.ultils;

import com.example.unicode.entity.Course;
import com.example.unicode.service.CloudinaryService;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExportCertificateUltils {
    private final S3Service s3Service;
    //   public List<String> getUrlCertificate(String userName, String password) {
//       try {
//           Map<String, String> request = Map.of("user_name", userName, "course_name", password);
//           org.springframework.http.HttpHeaders headers = new HttpHeaders();
//           headers.setContentType(MediaType.APPLICATION_JSON);
//
//           HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);
//
//           // Gọi FastAPI → nhận byte[]
//           ResponseEntity<byte[]> response = restTemplate.exchange(
//                   url,
//                   HttpMethod.POST,
//                   entity,
//                   byte[].class
//           );
//
//           byte[] imageBytes = response.getBody();
//
//           if (imageBytes == null) {
//               throw new RuntimeException("Không nhận được dữ liệu ảnh từ FastAPI");
//           }
//
//           // Convert sang MultipartFile
//           MultipartFile file =  new MockMultipartFile(
//                   "file",
//                   "certificate.jpg",
//                   MediaType.IMAGE_JPEG_VALUE,
//                   imageBytes
//           );
//           return cloudiaryUltils.getUrlCloudiary(file,"image");
//       } catch (Exception e) {
//           throw new RuntimeException("Lỗi khi gọi FastAPI: " + e.getMessage(), e);
//       }
//   }
    public String generateCertificate(String name, Course course) {

            try {

                InputStream is = getClass().getClassLoader()
                        .getResourceAsStream("certificate.html");

                String html = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                // replace data
                html = html.replace("${name}", name);
                html = html.replace("${course}", course.getTitle());
                html = html.replace("${date}", LocalDate.now().toString());
                html = html.replace("${instructor}", course.getInstructors().getName());

                ByteArrayOutputStream out = new ByteArrayOutputStream();

                HtmlConverter.convertToPdf(html, out);
                Files.write(Paths.get("test.pdf"), out.toByteArray());
                MultipartFile file = new MockMultipartFile(
                        "file",
                        "certificate.pdf",
                        MediaType.APPLICATION_PDF_VALUE,
                        out.toByteArray()
                );
            return s3Service.uploadPublic(file, "certificate");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi export certificate " + e.getMessage());
        }
    }
}
