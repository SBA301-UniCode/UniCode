package com.example.unicode.service;

import com.example.unicode.dto.request.ContentCreateRequest;
import com.example.unicode.dto.request.ContentUpdateRequest;
import com.example.unicode.dto.response.ContentResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface ContentService {

    ContentResponse create (ContentCreateRequest request);

    List<ContentResponse> getAllContentAndLesson(UUID lesonId);

    void delete(UUID lesonId);

    ContentResponse update(UUID contentId, ContentUpdateRequest request);
}
