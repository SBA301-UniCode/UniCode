package com.example.unicode.mapper;

import com.example.unicode.dto.request.FeedbackRequest;
import com.example.unicode.dto.request.UpdateFeedbackRequest;
import com.example.unicode.dto.response.FeedBackResponse;
import com.example.unicode.entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {ImageMapper.class,UserMapper.class})
public interface FeedBackMapper {
    @Mapping(target = "userResponse",source = "learner")
    @Mapping(target = "imageResponses",source ="images")
    FeedBackResponse entityToResponse(Feedback feedback);
    Feedback requestToEntity(FeedbackRequest feedback);
    Feedback updateRequestToEntity(UpdateFeedbackRequest request);
}
