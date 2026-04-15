package com.vshevchenko.resaleplatform.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.vshevchenko.resaleplatform.exception.AdNotFoundException;
import com.vshevchenko.resaleplatform.exception.CommentNotFoundException;
import com.vshevchenko.resaleplatform.exception.UserNotFoundException;

/**
 * Глобальный обработчик исключений для всего приложения.
 * <p>
 * Перехватывает исключения, возникающие в контроллерах, и возвращает
 * соответствующие HTTP-статусы с логированием ошибки.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение BadCredentialsException (неверные учетные данные).
     *
     * @param e исключение
     * @return ответ с кодом 401 (Unauthorized)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Ошибка аутентификации: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Обрабатывает общее исключение аутентификации.
     *
     * @param e исключение
     * @return ответ с кодом 401 (Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        log.error("Ошибка аутентификации: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Обрабатывает исключение AccessDeniedException (доступ запрещен).
     *
     * @param e исключение
     * @return ответ с кодом 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Доступ запрещен: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Обрабатывает NullPointerException.
     * Часто возникает при отсутствии аутентификации.
     *
     * @param e исключение
     * @return ответ с кодом 401 (Unauthorized)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        log.error("Null pointer exception - возможно отсутствует аутентификация", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Обрабатывает исключения, связанные с ненайденными сущностями.
     *
     * @param e исключение
     * @return ответ с кодом 404 (Not Found)
     */
    @ExceptionHandler({UserNotFoundException.class, AdNotFoundException.class, CommentNotFoundException.class})
    public ResponseEntity<?> handleNotFoundException(RuntimeException e) {
        log.error("Сущность не найдена: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Обрабатывает исключение IllegalArgumentException (неверные аргументы).
     *
     * @param e исключение
     * @return ответ с кодом 400 (Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Неверные данные: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    /**
     * Обрабатывает все остальные исключения.
     *
     * @param e исключение
     * @return ответ с кодом 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        log.error("Внутренняя ошибка сервера", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
