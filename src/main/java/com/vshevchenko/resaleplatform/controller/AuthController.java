package com.vshevchenko.resaleplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.vshevchenko.resaleplatform.dto.Login;
import com.vshevchenko.resaleplatform.dto.Register;
import com.vshevchenko.resaleplatform.service.AuthService;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * <p>
 * Предоставляет REST API для входа в систему и регистрации новых пользователей.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Tag(name = "Авторизация и регистрация")
public class AuthController {

    private final AuthService authService;

    /**
     * Аутентификация пользователя.
     * <p>
     * Проверяет учетные данные пользователя и при успехе возвращает 200 OK.
     * </p>
     *
     * @param login объект с логином и паролем
     * @return 200 OK при успешном входе, 401 при неверных данных
     */
    @Operation(
            summary = "Авторизация пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Login login) {
        return authService.login(login.getUsername(), login.getPassword())
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Регистрация нового пользователя.
     * <p>
     * Создает нового пользователя с указанными данными.
     * </p>
     *
     * @param register объект с данными для регистрации
     * @return 201 Created при успешной регистрации, 400 Bad Request при ошибке
     */
    @Operation(
            summary = "Регистрация пользователя",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
