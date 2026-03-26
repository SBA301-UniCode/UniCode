package com.example.unicode.service.impl;

import com.example.unicode.dto.request.AICodeAssistantRequest;
import com.example.unicode.service.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Service
public class GeminiServiceImpl implements GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;

    @Value("${gemini.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String askCodeAssistant(AICodeAssistantRequest request) {
        String prompt = buildPrompt(request);
        String url = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        try {
            // Build Gemini request body
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.3,
                            "maxOutputTokens", 1024
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Gemini API returned status {}", response.getStatusCode());
                throw new RuntimeException("Gemini AI không phản hồi. Vui lòng thử lại sau.");
            }

            // Parse response JSON
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                throw new RuntimeException("Gemini AI không trả về phản hồi.");
            }

            return textNode.asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Lỗi kết nối Gemini AI. Vui lòng thử lại sau.");
        }
    }

    /**
     * Build Vietnamese prompt matching the original FE implementation.
     */
    private String buildPrompt(AICodeAssistantRequest request) {
        String langLabel = "JAVA".equalsIgnoreCase(request.getLanguage()) ? "Java"
                : "PYTHON".equalsIgnoreCase(request.getLanguage()) ? "Python"
                : request.getLanguage();

        String base = String.format("""
                Bạn là một gia sư lập trình. Ngôn ngữ: %s.
                Bài toán: %s
                Code của học viên:
                ```%s
                %s
                ```""",
                langLabel,
                request.getDescription() != null ? request.getDescription() : "Không có mô tả",
                langLabel.toLowerCase(),
                request.getCode() != null ? request.getCode() : "// (chưa viết code)"
        );

        String mode = request.getMode() != null ? request.getMode() : "hint";

        return switch (mode) {
            case "hint" -> base + """
                    
                    
                    Hãy cho 1-2 gợi ý nhỏ để giúp học viên đi đúng hướng. KHÔNG tiết lộ đáp án hoặc code hoàn chỉnh. Hãy khuyến khích và tích cực. Trả lời bằng tiếng Việt.""";

            case "explain_error" -> base + "\n\nKết quả test:\n" + formatTestResults(request.getTestResults()) + """
                    
                    
                    Hãy giải thích TẠI SAO code bị lỗi ở các test case FAIL. Phân tích logic sai ở đâu. KHÔNG đưa ra code sửa hoàn chỉnh. Trả lời bằng tiếng Việt.""";

            case "review" -> base + """
                    
                    
                    Hãy review chất lượng code: naming, cấu trúc, hiệu năng, edge cases. Cho điểm từ 1-10 và gợi ý cải thiện. Trả lời bằng tiếng Việt.""";

            case "suggest_fix" -> {
                String testPart = request.getTestResults() != null && !request.getTestResults().isEmpty()
                        ? "\n\nKết quả test:\n" + formatTestResults(request.getTestResults())
                        : "";
                yield base + testPart + """
                        
                        
                        Hãy gợi ý HƯỚNG sửa (approach), KHÔNG cho code hoàn chỉnh. Chỉ ra phần nào cần thay đổi và tại sao. Trả lời bằng tiếng Việt.""";
            }

            default -> base + """
                    
                    
                    Hãy cho 1-2 gợi ý nhỏ để giúp học viên đi đúng hướng. KHÔNG tiết lộ đáp án hoặc code hoàn chỉnh. Trả lời bằng tiếng Việt.""";
        };
    }

    private String formatTestResults(List<AICodeAssistantRequest.TestResultItem> results) {
        if (results == null || results.isEmpty()) {
            return "Chưa có kết quả test";
        }
        return IntStream.range(0, results.size())
                .mapToObj(i -> {
                    var t = results.get(i);
                    return String.format("Case %d: Input: %s, Expected: %s, Actual: %s → %s",
                            i + 1, t.getInput(), t.getExpected(), t.getActual(),
                            t.isPassed() ? "PASS ✅" : "FAIL ❌");
                })
                .reduce((a, b) -> a + "\n" + b)
                .orElse("Chưa có kết quả test");
    }

    @Override
    public String askCourseRecommendation(String userMessage, String coursesCatalog) {
        String prompt = String.format("""
                Bạn là tư vấn viên khóa học cho nền tảng học trực tuyến UniCode.
                
                DANH SÁCH KHÓA HỌC HIỆN CÓ:
                %s
                
                CÂU HỎI CỦA NGƯỜI DÙNG: "%s"
                
                QUY TẮC BẮT BUỘC:
                1. Nếu câu hỏi liên quan đến khóa học, lập trình, công nghệ, học tập → gợi ý 1-3 khóa học phù hợp nhất từ danh sách trên.
                2. Với mỗi khóa học gợi ý, PHẢI dùng format: [COURSE:courseId] (ví dụ: [COURSE:abc-123]) để hệ thống tạo link.
                3. Nếu câu hỏi KHÔNG liên quan đến khóa học hoặc học tập → trả lời: "Xin lỗi, câu hỏi này nằm ngoài phạm vi tư vấn khóa học. Tôi chỉ có thể hỗ trợ về các khóa học trên nền tảng UniCode."
                4. Trả lời bằng tiếng Việt, thân thiện, ngắn gọn (tối đa 200 từ).
                5. Đề cập tên khóa học, giá, và giảng viên khi gợi ý.
                6. Nếu không tìm thấy khóa học phù hợp → nói rằng hiện tại chưa có khóa học phù hợp và gợi ý xem toàn bộ danh sách.
                """, coursesCatalog, userMessage);

        String url = baseUrl + "/" + model + ":generateContent?key=" + apiKey;

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.4,
                            "maxOutputTokens", 512
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Gemini AI không phản hồi. Vui lòng thử lại sau.");
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode textNode = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                throw new RuntimeException("Gemini AI không trả về phản hồi.");
            }

            return textNode.asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling Gemini API for course chat", e);
            throw new RuntimeException("Lỗi kết nối Gemini AI. Vui lòng thử lại sau.");
        }
    }
}
