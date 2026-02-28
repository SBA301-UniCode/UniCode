package com.example.unicode.mapper;


import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(source = "videoUrl", target = "url")
    @Mapping(source = "content.contentId", target = "contentId")
    @Mapping(source = "videoId", target = "videoId")
    @Mapping(source = "duration", target = "duration")
    @Mapping(source = "publicId", target = "publicId")
    VideoResponse toResponse(Video video);

    List<VideoResponse> toResponseList(List<Video> videos);
}
