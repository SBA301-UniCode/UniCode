package com.example.unicode.mapper;

import com.example.unicode.dto.request.PrivilegeCreateRequest;
import com.example.unicode.dto.request.PrivilegeUpdateRequest;
import com.example.unicode.dto.response.PrivilegeResponse;
import com.example.unicode.entity.Privilege;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PrivilegeMapper {

    Privilege toEntity(PrivilegeCreateRequest request);

    PrivilegeResponse toResponse(Privilege privilege);

    List<PrivilegeResponse> toResponseList(List<Privilege> privileges);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(PrivilegeUpdateRequest request, @MappingTarget Privilege privilege);
}

