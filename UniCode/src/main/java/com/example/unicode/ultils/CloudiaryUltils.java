package com.example.unicode.ultils;

import com.example.unicode.configuration.CloudinaryConfig;
import com.example.unicode.dto.request.CloudiaryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CloudiaryUltils {
    @Value("${cloudinary.url}")
    private String url;
    private final CloudinaryConfig cloudinaryConfig;
    private RestTemplate restTemplate = new RestTemplate();

    public List<String> getUrlCloudiary(MultipartFile file, String folder) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", cloudinaryConfig.getApiKey());

        long timestamp = System.currentTimeMillis() / 1000L;
        body.add("timestamp", String.valueOf(timestamp));

        body.add("folder", folder);
        body.add("signature", generateSignature(folder,timestamp));

        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload_file";
            }
        };
        body.add("file", fileAsResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                url,
                requestEntity,
                Map.class
        );

        log.info("Response {}", response.getBody());
        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null) {

            String publicId = (String) responseBody.get("public_id");
            String secureUrl = (String) responseBody.get("secure_url");
            return List.of(publicId, secureUrl);
        }

        return List.of();
    }

    public String generateSignature(String folder, long timestamp) {
        String data = "folder=" + folder + "&timestamp=" + timestamp + cloudinaryConfig.getApiSecret();
        return DigestUtils.sha1Hex(data);
    }
}
