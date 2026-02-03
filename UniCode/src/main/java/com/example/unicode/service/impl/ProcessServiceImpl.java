package com.example.unicode.service.impl;

import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.request.ProcessRequest;
import com.example.unicode.dto.response.TrackingResponse;
import com.example.unicode.entity.*;
import com.example.unicode.entity.Process;
import com.example.unicode.enums.StatusContent;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.ProcessMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.tools.Trace;
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
    private final  ContentRepo contentRepo;

    @Override
    public TrackingResponse.ProcessResponse trackProcessContent(ProcessRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND)
        );
        Content content = contentRepo.findById(request.getContentId()).orElseThrow(
                ()-> new AppException(ErrorCode.CONTENT_NOT_FOUND)
        );
        Process process = processRepository.findByContentAndEnrollment(content,enrollment);
        if(process == null)
        {
             process = Process.builder()
                    .enrollment(enrollment)
                    .content(content)
                    .build();
        }
        process.setStatusContent(request.getStatus());
        processStatusLesson(content.getLesson(),enrollment);
        processStatusChapter(content.getLesson().getChapter(),enrollment);
        return processMapper.entityToResponse(processRepository.save(process));
    }
    public void processStatusLesson(Lesson lesson, Enrollment enrollment) {
        Process process  = processRepository.findByLessonAndEnrollment(lesson,enrollment);
        TrackingResponse trackingResponse = getProcessOfLesson(TrackingRequest.builder()
                .id(lesson.getLessonId())
                .enrollmentId(enrollment.getEnrollmentId())
                .build());
        if(process == null)
        {
            process = Process.builder()
                    .lesson(lesson)
                    .enrollment(enrollment)
                    .build();
        }
        process.setStatusContent(trackingResponse.getPercentComplete() == 100 ? StatusContent.COMPLETED : StatusContent.IN_PROCESS);
        processRepository.save(process);
    }
    public void processStatusChapter(Chapter chapter, Enrollment enrollment) {
        Process process  = processRepository.findByChapterAndEnrollment(chapter, enrollment);
        TrackingResponse trackingResponse = getProcessOfChapter(TrackingRequest.builder()
                .id(chapter.getChapterId())
                .enrollmentId(enrollment.getEnrollmentId())
                .build());
        if(process == null)
        {
            process = Process.builder()
                    .chapter(chapter)
                    .enrollment(enrollment)
                    .build();
        }
        process.setStatusContent(trackingResponse.getPercentComplete() == 100 ? StatusContent.COMPLETED : StatusContent.IN_PROCESS);
        processRepository.save(process);
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
              TrackingResponse.ProcessResponse processResponse = processMapper.entityToResponse(processRepository.findByContentAndEnrollment_EnrollmentId(
                      content, request.getEnrollmentId()));

              if (processResponse != null) {
                  if (processResponse.getStatusContent().equals(StatusContent.COMPLETED)) {
                      success++;
                  }
                  processResponse.setId(content.getContentId());
                  processRespons.add(processResponse);
              }
          }
        }
        return TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /contents.size()  : 0)
                .processResponseList(processRespons)
                .build();
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
                if (processResponse != null) {
                    if (processResponse.getStatusContent().equals(StatusContent.COMPLETED)) {
                        success++;
                    }
                    processResponse.setId(lesson.getLessonId());
                    processRespons.add(processResponse);
                }
            }
        }
        return TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /lessonList.size()  : 0)
                .processResponseList(processRespons)
                .build();
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
                if (processResponse != null) {
                    if (processResponse.getStatusContent().equals(StatusContent.COMPLETED)) {
                        success++;
                    }
                    processResponse.setId(chapter.getChapterId());
                    processRespons.add(processResponse);
                }
            }
        }
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId()).orElseThrow(
                ()-> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND)
        );
        TrackingResponse response = TrackingResponse.builder()
                .percentComplete(success > 0 ? ((double) success *100) /chapters.size()  : 0)
                .processResponseList(processRespons)
                .build();
        enrollment.setStatusCourse(response.getPercentComplete() == 100 ? StatusCourse.COMPLETED: StatusCourse.IN_PROGRESS);
      enrollmentRepository.save(enrollment);
        return response;
    }
}
