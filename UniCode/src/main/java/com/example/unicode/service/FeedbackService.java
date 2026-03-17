package com.example.unicode.service;

import com.example.unicode.dto.request.FeedbackRequest;
import com.example.unicode.dto.request.UpdateFeedbackRequest;
import com.example.unicode.dto.response.FeedBackResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    FeedBackResponse  createFeedback(    UUID courseId,FeedbackRequest request, List<MultipartFile> list);
    Page<FeedBackResponse> getFeedbakcCourse(UUID courseId,int page, int size);
    boolean canFeedback(UUID courseId);
    boolean canEditAndDelete(UUID feedbackId);
    FeedBackResponse updateFeedback(UpdateFeedbackRequest request,UUID feedbackId, List<MultipartFile> list);
    void delete(UUID feedbackId);
}
