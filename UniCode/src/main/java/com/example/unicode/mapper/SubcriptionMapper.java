package com.example.unicode.mapper;

import com.example.unicode.dto.response.SubcriptionResponse;
import com.example.unicode.entity.Subcription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
@Mapper(componentModel = "spring")
public interface SubcriptionMapper {
    @Mapping(target = "courseraId",source = "course.courseId")
    @Mapping(target = "buyerId",source = "learner.userId")
    SubcriptionResponse entityToResponse(Subcription subcription);
}
