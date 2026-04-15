package com.vshevchenko.resaleplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import com.vshevchenko.resaleplatform.dto.Register;
import com.vshevchenko.resaleplatform.dto.UpdateUser;
import com.vshevchenko.resaleplatform.dto.User;
import com.vshevchenko.resaleplatform.entity.UserEntity;

/**
 * Маппер для преобразования между UserEntity и соответствующими DTO.
 * Использует MapStruct для генерации реализации во время компиляции.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUserDto(UserEntity entity);

    UserEntity toUserEntity(User dto);

    @Mapping(target = "email", source = "username")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)

    UserEntity toUserEntity(Register registerDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "ads", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateUserEntityFromDto(UpdateUser dto, @MappingTarget UserEntity entity);
}
