package com.example.unicode.mapper;

import com.example.unicode.dto.response.ImageResponse;
import com.example.unicode.entity.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageResponse entityToResponse(Image image);
}
