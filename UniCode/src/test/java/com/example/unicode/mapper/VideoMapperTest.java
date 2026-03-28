package com.example.unicode.mapper;

import com.example.unicode.entity.Content;
import com.example.unicode.entity.Video;
import com.example.unicode.enums.VideoStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VideoMapperTest {

    private final VideoMapper mapper = Mappers.getMapper(VideoMapper.class);

    @Test
    void toResponseShouldMapIds() {
        Content content = new Content();
        content.setContentId(UUID.randomUUID());

        Video video = new Video();
        video.setVideoId(UUID.randomUUID());
        video.setContent(content);
        video.setStatus(VideoStatus.READY);

        var response = mapper.toResponse(video);
        assertEquals(video.getVideoId(), response.getVideoId());
        assertEquals(content.getContentId(), response.getContentId());
        assertEquals(VideoStatus.READY, response.getStatus());

        assertEquals(1, mapper.toResponseList(List.of(video)).size());
        assertNull(mapper.toResponseList(null));
    }

    @Test
    void toResponseShouldHandleNullBranches() {
        assertNull(mapper.toResponse(null));

        Video withoutContent = new Video();
        withoutContent.setVideoId(UUID.randomUUID());
        withoutContent.setContent(null);
        assertNull(mapper.toResponse(withoutContent).getContentId());

        Video withContentNoId = new Video();
        Content contentNoId = new Content();
        withContentNoId.setContent(contentNoId);
        assertNull(mapper.toResponse(withContentNoId).getContentId());
    }

    @Test
    void privateHelperShouldReturnNullWhenVideoIsNull() throws Exception {
        Method helper = mapper.getClass().getDeclaredMethod("videoContentContentId", Video.class);
        helper.setAccessible(true);

        Object result = helper.invoke(mapper, new Object[]{null});

        assertNull(result);
    }
}
