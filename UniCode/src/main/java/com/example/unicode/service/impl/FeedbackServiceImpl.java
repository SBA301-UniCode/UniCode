package com.example.unicode.service.impl;

import com.example.unicode.dto.request.FeedbackRequest;
import com.example.unicode.dto.request.UpdateFeedbackRequest;
import com.example.unicode.dto.response.FeedBackResponse;
import com.example.unicode.entity.Course;
import com.example.unicode.entity.Feedback;
import com.example.unicode.entity.Image;
import com.example.unicode.enums.StatusCourse;
import com.example.unicode.enums.StatusPayment;
import com.example.unicode.exception.AppException;
import com.example.unicode.exception.ErrorCode;
import com.example.unicode.mapper.FeedBackMapper;
import com.example.unicode.repository.*;
import com.example.unicode.service.EnrollmentService;
import com.example.unicode.service.FeedbackService;
import com.example.unicode.service.SubcriptionService;
import com.example.unicode.service.UserService;
import com.example.unicode.ultils.CloudiaryUltils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CourseRepository courseRepository;
    private final ImageRepository imageRepository;
    private final CloudiaryUltils cloudiaryUltils;
    private final UserService userService;
    private final FeedBackMapper feedBackMapper;
    private final EnrollmentRepository enrollmentRepository;
    public FeedBackResponse createFeedback(UUID courseId, FeedbackRequest request, List<MultipartFile> file) {
        Feedback feedback = feedBackMapper.requestToEntity(request);
        Course course = courseRepository.findById(courseId).orElseThrow(

                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        feedback.setLearner(userService.getUsers());
        feedback.setCourse(course);
        addImage(file,feedback);
        return feedBackMapper.entityToResponse(feedbackRepository.save(feedback));
    }

    @Override
    public Page<FeedBackResponse> getFeedbakcCourse(UUID courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page -1 , size, Sort.by("createdAt").descending());
        Course course = courseRepository.findById(courseId).orElseThrow(

                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        Page<Feedback> feedbacks = feedbackRepository.findFeedbackByCourse(course,pageable);

        return feedbacks.map(feedBackMapper::entityToResponse);
    }

    @Override
    public boolean canFeedback(UUID courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(

                ()-> new AppException(ErrorCode.COURSE_NOT_FOUND)
        );
        boolean complete = enrollmentRepository.existsByCourseAndLearnerAndStatusCourse(course,userService.getUsers(), StatusCourse.COMPLETED);

        return !feedbackRepository.existsByLearnerAndCourse(userService.getUsers(), course) &&  complete;
    }

    @Override
    public boolean canEditAndDelete(UUID feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElseThrow(
                ()-> new AppException(ErrorCode.FEEDBACK_NOT_FOUND)
        );
        return feedback.getLearner().equals(userService.getUsers());
    }

    @Override
    public FeedBackResponse updateFeedback(UpdateFeedbackRequest request, UUID feedbackId, List<MultipartFile> file) {
        Feedback feedback =feedbackRepository.findByFeedBackId(feedbackId);
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        feedbackRepository.save(feedback);
        if(request.getImageRemoveId() != null)
        {
            removeImage(request.getImageRemoveId(),feedback);
        }
        addImage(file,feedback);
        return feedBackMapper.entityToResponse(feedbackRepository.save(feedback));
    }

    @Override
    public void delete(UUID feedbackId) {
       feedbackRepository.deleteById(feedbackId);
    }
    public void removeImage(List<UUID> image,Feedback feedback)
    {
        List<Image> images = image.stream().map((i)-> imageRepository.findById(i).get()).toList();
        feedback.getImages().removeAll(
                images
        );
    }
    public void addImage(List<MultipartFile> file,Feedback feedback) {
        if (file != null && !file.isEmpty()) {
            for (MultipartFile f : file) {
                try {
                    List<String> list = cloudiaryUltils.getUrlCloudiary(f, "image");
                    if (list.size() == 2) {
                        Image image = Image.builder()
                                .imageUrl(list.get(1))
                                .publicId(list.get(0))
                                .feedback(feedback)
                                .build();
                        feedback.getImages().add(image);
                    }
                } catch (Exception e) {
                    throw new AppException(ErrorCode.CAN_UPLOAD_MATERIAL);
                }
            }
        }
    }
}
