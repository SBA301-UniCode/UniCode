package com.example.unicode.mapper;

import com.example.unicode.dto.request.UserCreateRequest;
import com.example.unicode.dto.request.UserUpdateRequest;
import com.example.unicode.dto.response.UserResponse;
import com.example.unicode.entity.Users;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "rolesList", ignore = true)
    @Mapping(target = "password", ignore = true)
    Users toEntity(UserCreateRequest request);

    @Mapping(source = "rolesList", target = "roles")
    UserResponse toResponse(Users user);

    List<UserResponse> toResponseList(List<Users> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "rolesList", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget Users user);
}

