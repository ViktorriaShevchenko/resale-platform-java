package com.vshevchenko.resaleplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для списка объявлений.
 * Содержит общее количество объявлений и их список.
 */
@Data
@Schema(description = "Список объявлений")
public class Ads {

    @Schema(description = "общее количество объявлений")
    private Integer count;

    @Schema(description = "список объявлений")
    private List<Ad> results;
}
