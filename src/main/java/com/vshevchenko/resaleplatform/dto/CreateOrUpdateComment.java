package com.vshevchenko.resaleplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для создания или обновления комментария.
 * Используется при POST и PATCH запросах к /ads/{id}/comments.
 */
@Data
@Schema(description = "Создание или обновление комментария")
public class CreateOrUpdateComment {

    @Schema(description = "текст комментария", minLength = 8, maxLength = 64)
    private String text;
}
