package com.sa.event_mng.modules.identity.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.sa.event_mng.modules.identity.application.dto.request.UserCreateRequest;
import com.sa.event_mng.modules.identity.application.dto.request.UserUpdateRequest;
import com.sa.event_mng.modules.identity.application.dto.response.UserResponse;
import com.sa.event_mng.modules.identity.domain.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    User toUser(UserCreateRequest request);

    @Mapping(target = "createdAt", source = "createdAt")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
