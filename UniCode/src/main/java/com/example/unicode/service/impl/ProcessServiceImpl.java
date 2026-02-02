package com.example.unicode.service.impl;

import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.request.ProcessRequest;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.entity.*;
import com.example.unicode.enums.StatusContent;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ProcessMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {
    private final ProcessRepository processRepository;
    private final LessonRepository lessonRepository;
    private final ProcessMapper processMapper;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public TrackingResponse.ProcessResponse trackProcessContent(ProcessRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND)
        );
//        if(!processRepository.existsProcessByContent_ContentId(request.getContentId()))
//        {
//
//            Process p = Process.builder()
//                    .
//                    .build();
//        }
        return null;
    }

    @Override
    public TrackingResponse getProcessOfLesson(TrackingRequest request) {
        Lesson lesson = lessonRepository.findById(request.getId()).orElseThrow(
                () -> new AppException(ErrorCode.LESSON_NOT_FOUND)
        );
        List<Content> contents = lesson.getContentList();
        List<TrackingResponse.ProcessResponse> processRespons = new ArrayList<>();
        long success = 0 ;
        if (contents != null && !contents.isEmpty()) {
          for(Content content : contents) {
               TrackingResponse.ProcessResponse processResponse =  processMapper.entityToResponse( processRepository.findByContentAndEnrollment_EnrollmentId(
                                        content, request.getEnrollmentId()));
             if(processResponse != null && processResponse.getStatusContent().equals(StatusContent.COMPLETED))
             {
                 success++;
             }
            }
        }
        TrackingResponse response = TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /contents.size()  : 0)
                .processResponseList(processRespons)
                .build();
        return response;
    }

    @Override
    public TrackingResponse  getProcessOfChapter(TrackingRequest request) {
        Chapter chapter = chapterRepository.findById(request.getId()).orElseThrow(
                ()-> new AppException(ErrorCode.CHAPTER_NOT_FOUND)
        );
        List<Lesson> lessonList = chapter.getLessonList();
        List<TrackingResponse.ProcessResponse> processRespons = new ArrayList<>();
        long success = 0 ;
        if (lessonList != null && !lessonList.isEmpty()) {
            for(Lesson lesson : lessonList) {
                TrackingResponse.ProcessResponse processResponse =  processMapper.entityToResponse( processRepository.findByLessonAndEnrollment_EnrollmentId(
                        lesson, request.getEnrollmentId()));
                if(processResponse != null && processResponse.getStatusContent().equals(StatusContent.COMPLETED))
                {
                    success++;
                }
            }
        }
        TrackingResponse response = TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /lessonList.size()  : 0)
                .processResponseList(processRespons)
                .build();
        return response;
    }

    @Override
    public TrackingResponse getProcessOfCourses(TrackingRequest request) {
        Course course = courseRepository.findById(request.getId()).orElseThrow(
                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        List<Chapter> chapters = course.getChapterList();
        List<TrackingResponse.ProcessResponse> processRespons = new ArrayList<>();
        long success = 0 ;
        if (chapters != null && !chapters.isEmpty()) {
            for(Chapter chapter : chapters) {
                TrackingResponse.ProcessResponse processResponse =  processMapper.entityToResponse(
                        processRepository.findByChapterAndEnrollment_EnrollmentId(
                        chapter, request.getEnrollmentId()));
                if(processResponse != null && processResponse.getStatusContent().equals(StatusContent.COMPLETED))
                {
                    success++;
                }
            }
        }
        TrackingResponse response = TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /chapters.size()  : 0)
                .processResponseList(processRespons)
                .build();
        return response;
    }
}
