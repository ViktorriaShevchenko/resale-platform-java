package com.vshevchenko.resaleplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.ExtendedAd;
import com.vshevchenko.resaleplatform.entity.AdEntity;

import java.util.List;

/**
 * Маппер для преобразования между AdEntity и соответствующими DTO.
 * Использует MapStruct для генерации реализации во время компиляции.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class})
public interface AdMapper {

    @Mapping(target = "author", source = "author.id")
    Ad toAdDto(AdEntity entity);

    @Mapping(target = "pk", source = "pk")
    @Mapping(target = "authorFirstName", source = "author.firstName")
    @Mapping(target = "authorLastName", source = "author.lastName")
    @Mapping(target = "email", source = "author.email")
    @Mapping(target = "phone", source = "author.phone")
    ExtendedAd toExtendedAdDto(AdEntity entity);

    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    AdEntity toAdEntity(CreateOrUpdateAd dto);

    List<Ad> toAdDtoList(List<AdEntity> entities);

    @Mapping(target = "pk", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateAdEntityFromDto(CreateOrUpdateAd dto, @MappingTarget AdEntity entity);
}
