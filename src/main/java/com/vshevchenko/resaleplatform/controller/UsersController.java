package com.vshevchenko.resaleplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.NewPassword;
import com.vshevchenko.resaleplatform.dto.UpdateUser;
import com.vshevchenko.resaleplatform.dto.User;
import com.vshevchenko.resaleplatform.service.UserService;

/**
 * Контроллер для управления профилем пользователя.
 * <p>
 * Предоставляет REST API для получения и обновления информации о пользователе,
 * смены пароля и обновления аватара.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UsersController {

    private final UserService userService;

    /**
     * Обновляет пароль текущего пользователя.
     * <p>
     * Проверяет старый пароль перед установкой нового.
     * </p>
     *
     * @param newPassword объект с текущим и новым паролем
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return пустой ответ с кодом 200
     */
    @Operation(
            summary = "Обновление пароля",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PostMapping("/set_password")
    public ResponseEntity<Void> setPassword(@RequestBody NewPassword newPassword,
                                            Authentication authentication) {
        userService.updatePassword(authentication.getName(), newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * Получает информацию о текущем авторизованном пользователе.
     *
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return информация о пользователе
     */
    @Operation(
            summary = "Получение информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<User> getUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getUser(authentication.getName()));
    }

    /**
     * Обновляет информацию о текущем пользователе (имя, фамилию, телефон).
     *
     * @param updateUser новые данные пользователя
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return обновленные данные пользователя
     */
    @Operation(
            summary = "Обновление информации об авторизованном пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser,
                                                 Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(authentication.getName(), updateUser));
    }

    /**
     * Обновляет аватар текущего пользователя.
     *
     * @param image новый файл аватара
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return пустой ответ с кодом 200
     */
    @Operation(
            summary = "Обновление аватара авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateUserImage(@RequestParam("image") MultipartFile image,
                                                Authentication authentication) {
        userService.updateUserImage(authentication.getName(), image);
        return ResponseEntity.ok().build();
    }
}
