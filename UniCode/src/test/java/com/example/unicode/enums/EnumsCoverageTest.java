package com.example.unicode.enums;

import com.example.unicode.entity.PracticeExam;
import com.example.unicode.entity.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumsCoverageTest {

    @Test
    void shouldLoadAllEnums() {
        assertTrue(ContentType.values().length > 0);
        assertTrue(VideoStatus.values().length > 0);
        assertTrue(QuestionType.values().length > 0);
        assertTrue(StatusPayment.values().length > 0);
        assertTrue(StatusContent.values().length > 0);
        assertTrue(StatusCourse.values().length > 0);
        assertTrue(PracticeExam.Difficulty.values().length > 0);
        assertTrue(PracticeExam.CodeLanguage.values().length > 0);
        assertTrue(TestCase.OutputType.values().length > 0);
    }
}

