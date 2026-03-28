package com.example.unicode.dto.response;

import com.example.unicode.entity.PracticeExam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PracticeStartResponse {
    private UUID practiceId;       // ID của bài practice exam
    private UUID submissionId;     // ID của lần làm bài (quan trọng để nộp và xem kết quả)
    private String title;          // tiêu đề bài tập
    private String description;    // mô tả bài tập
    private String starterCode;    // code mẫu cho học viên
    private PracticeExam.CodeLanguage language;       // JAVA, PYTHON
    private PracticeExam.Difficulty difficulty;     // EASY, NORMAL, HARD
    private List<TestCaseResponse> visibleTestCases; // chỉ test case không hidden
}
