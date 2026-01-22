package com.example.unicode.mapper;

import com.example.unicode.dto.request.RoleCreateRequest;
import com.example.unicode.dto.request.RoleUpdateRequest;
import com.example.unicode.dto.response.RoleResponse;
import com.example.unicode.entity.Role;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PrivilegeMapper.class})
public interface RoleMapper {

    @Mapping(target = "privileges", ignore = true)
    Role toEntity(RoleCreateRequest request);

    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "privileges", ignore = true)
    void updateEntity(RoleUpdateRequest request, @MappingTarget Role role);
}

