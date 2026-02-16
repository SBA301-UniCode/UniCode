package com.example.unicode.mapper;


import com.example.unicode.dto.response.VideoResponse;
import com.example.unicode.entity.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    @Mapping(source = "content.contentId", target = "contentId")
    VideoResponse toResponse(Video video);
    List<VideoResponse> toResponseList(List<Video> videos);
}
