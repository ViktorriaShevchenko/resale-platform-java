package com.vshevchenko.resaleplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import com.vshevchenko.resaleplatform.dto.Comment;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateComment;
import com.vshevchenko.resaleplatform.entity.CommentEntity;

import java.util.List;

/**
 * Маппер для преобразования между CommentEntity и соответствующими DTO.
 * Использует MapStruct для генерации реализации во время компиляции.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "author", source = "author.id")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorImage", source = "author.image")
    Comment toCommentDto(CommentEntity entity);

    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    CommentEntity toCommentEntity(CreateOrUpdateComment dto);

    List<Comment> toCommentDtoList(List<CommentEntity> entities);

    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "ad", ignore = true)
    void updateCommentEntityFromDto(CreateOrUpdateComment dto, @MappingTarget CommentEntity entity);
}
