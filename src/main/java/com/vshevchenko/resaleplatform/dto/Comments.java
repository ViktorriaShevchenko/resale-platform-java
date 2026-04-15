package com.vshevchenko.resaleplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO для списка комментариев.
 * Содержит общее количество комментариев и их список.
 */
@Data
@Schema(description = "Список комментариев")
public class Comments {

    @Schema(description = "общее количество комментариев")
    private Integer count;

    @Schema(description = "список комментариев")
    private List<Comment> results;
}
