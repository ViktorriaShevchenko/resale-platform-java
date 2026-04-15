package com.vshevchenko.resaleplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для смены пароля.
 * Используется при POST запросе к /users/set_password.
 */
@Data
@Schema(description = "Смена пароля")
public class NewPassword {

    @Schema(description = "текущий пароль", minLength = 8, maxLength = 16)
    private String currentPassword;

    @Schema(description = "новый пароль", minLength = 8, maxLength = 16)
    private String newPassword;
}
