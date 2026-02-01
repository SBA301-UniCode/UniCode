package com.example.unicode.service;

import com.example.unicode.dto.request.SearchSubcriptionRequest;
import com.example.unicode.dto.request.SubcriptionReportRequest;
import com.example.unicode.dto.response.SubcriptionReportResponse;
import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.entity.Subcription;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;


import java.util.UUID;

public interface SubcriptionService {
   String buyCourses(UUID courseId);
   SubcriptionResponse updateStatus(HttpServletRequest request);
   Page<SubcriptionResponse> getAll(SearchSubcriptionRequest request, int page, int size );
   SubcriptionReportResponse report(SubcriptionReportRequest request);
   SubcriptionResponse getById(UUID id);
}
