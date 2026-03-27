package com.example.unicode.mapper;

import com.example.unicode.entity.Content;
import com.example.unicode.entity.Document;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DocumentMapperTest {

    private final DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

    @Test
    void toResponseShouldMapDocumentAndContentId() {
        Content content = new Content();
        content.setContentId(UUID.randomUUID());

        Document document = new Document();
        document.setDocumentId(UUID.randomUUID());
        document.setDocumentUrl("https://doc");
        document.setTitle("Doc A");
        document.setContent(content);

        var response = mapper.toResponse(document);
        assertEquals("https://doc", response.getDocumentUrl());
        assertEquals("Doc A", response.getTitle());
        assertEquals(content.getContentId(), response.getContentId());

        assertEquals(1, mapper.toResponseList(List.of(document)).size());
        assertNull(mapper.toResponseList(null));
    }

    @Test
    void toResponseShouldHandleNullBranches() {
        assertNull(mapper.toResponse(null));

        Document noContent = new Document();
        noContent.setDocumentId(UUID.randomUUID());
        assertNull(mapper.toResponse(noContent).getContentId());

        Document withContentNoId = new Document();
        Content contentNoId = new Content();
        withContentNoId.setContent(contentNoId);
        assertNull(mapper.toResponse(withContentNoId).getContentId());
    }

    @Test
    void privateHelperShouldReturnNullWhenDocumentIsNull() throws Exception {
        Method helper = mapper.getClass().getDeclaredMethod("documentContentContentId", Document.class);
        helper.setAccessible(true);

        Object result = helper.invoke(mapper, new Object[]{null});

        assertNull(result);
    }
}
