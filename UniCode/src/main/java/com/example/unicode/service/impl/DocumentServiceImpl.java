package com.example.unicode.service.impl;

import com.example.unicode.dto.request.DocumentCreateRequest;
import com.example.unicode.dto.request.DocumentUpdateRequest;
import com.example.unicode.dto.response.DocumentResponse;
import com.example.unicode.entity.Content;
import com.example.unicode.entity.Document;
import com.example.unicode.entity.Lesson;
import com.example.unicode.enums.ContentType;
import com.example.unicode.mapper.DocumentMapper;
import com.example.unicode.repository.ContentRepo;
import com.example.unicode.repository.DocumentRepository;
import com.example.unicode.repository.LessonRepository;
import com.example.unicode.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final ContentRepo contentRepository;
    private final DocumentMapper documentMapper;
    private final LessonRepository lessonRepository;

    @Override
    public DocumentResponse create(DocumentCreateRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        Content  content = new Content();
        content.setLesson(lesson);
        content.setContentType(ContentType.DOCUMENT);
        content = contentRepository.save(content);
        Document document = new Document();
        document.setDocumentUrl(request.getDocumentUrl());
        document.setTitle(request.getTitle());
        document.setContent(content);
        return documentMapper.toResponse(documentRepository.save(document));

    }

    @Override
    public List<DocumentResponse> getAllDocumentByLessonId(UUID lessonId) {
        List<Document> documents = documentRepository.findAllByContent_Lesson_LessonId(lessonId);
        return documentMapper.toResponseList(documents);
    }

    @Override
    public void delete(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        UUID contentId = document.getContent().getContentId();
        documentRepository.delete(document);
        contentRepository.deleteById(contentId);

    }

    @Override
    public DocumentResponse update(UUID documentId, DocumentUpdateRequest request) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        document.setTitle(request.getTitle());
        document.setDocumentUrl(request.getDocumentUrl());

        return documentMapper.toResponse(documentRepository.save(document));
    }
}
