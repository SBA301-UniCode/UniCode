package com.example.unicode.dto.response;

import com.example.unicode.entity.Course;
import com.example.unicode.entity.Image;
import com.example.unicode.entity.Users;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class FeedBackResponse {
    private UUID feedBackId;
    private String comment;
    private int rating;
    private UserResponse userResponse;
    private List<ImageResponse> imageResponses;
}
