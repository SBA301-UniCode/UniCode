package com.example.unicode.service;

import com.example.unicode.dto.request.TrackingRequest;
import com.example.unicode.dto.request.ProcessRequest;
import com.example.unicode.dto.response.TrackingResponse;

import java.util.List;

public interface ProcessService {
    TrackingResponse.ProcessResponse trackProcessContent(ProcessRequest request);
    TrackingResponse getProcessOfLesson(TrackingRequest request);
    TrackingResponse getProcessOfChapter(TrackingRequest request);
    TrackingResponse getProcessOfCourses(TrackingRequest request);
}
