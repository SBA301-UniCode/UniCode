package com.example.unicode.service;

import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.dto.response.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {

    DocumentResponse create (DocumentCreateRequest request);

     List<DocumentResponse> getAllDocumentByLessonId(UUID lesonId);

     void delete(UUID lesonId);

     DocumentResponse update(UUID documentId, DocumentUpdateRequest request);

}
