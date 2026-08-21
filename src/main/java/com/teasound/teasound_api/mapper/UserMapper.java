package com.teasound.teasound_api.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.teasound.teasound_api.domain.User;
import com.teasound.teasound_api.dto.request.UpdateInfoUserRequest;
import com.teasound.teasound_api.dto.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "premium", target = "isPremium")
    UserResponse toUserResponse(User user);

    @Mapping(source = "name", target = "displayName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UpdateInfoUserRequest request);

    User toUser(UserResponse userResponse);
}
