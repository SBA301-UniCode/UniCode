package com.example.unicode.service.impl;

import com.example.unicode.entity.CourseMindMap;
import com.example.unicode.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseMindMapServiceImplTest {

    @Mock
    private CourseMindMapRepository mindMapRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CourseMindMapServiceImpl courseMindMapService;

    @Test
    void getUserTreeShouldReturnStoredTreeWhenExists() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        CourseMindMap mm = CourseMindMap.builder().treeData("{\"nodes\":[]}").build();
        when(mindMapRepository.findByUser_UserIdAndCourse_CourseId(userId, courseId)).thenReturn(Optional.of(mm));

        String tree = courseMindMapService.getUserTree(userId, courseId);

        assertEquals("{\"nodes\":[]}", tree);
    }
}

