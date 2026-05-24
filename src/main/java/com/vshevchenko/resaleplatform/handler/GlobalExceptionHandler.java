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

import javax.validation.ConstraintViolationException;

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
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        log.error("Ошибка аутентификации: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Неверные учетные данные");
    }

    /**
     * Обрабатывает общее исключение аутентификации.
     *
     * @param e исключение
     * @return ответ с кодом 401 (Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        log.error("Ошибка аутентификации: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Ошибка аутентификации");
    }

    /**
     * Обрабатывает исключение AccessDeniedException (доступ запрещен).
     *
     * @param e исключение
     * @return ответ с кодом 403 (Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        log.error("Доступ запрещен: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Доступ запрещен");
    }

    /**
     * Обрабатывает NullPointerException.
     * <p>
     * В текущей реализации используется как временная защита
     * для случаев, когда отсутствует аутентификация.
     * </p>
     *
     * @param e исключение
     * @return ответ с кодом 401 (Unauthorized)
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException e) {
        log.error("NullPointerException: возможно отсутствует аутентификация", e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Пользователь не аутентифицирован");
    }

    /**
     * Обрабатывает исключения, связанные с ненайденными сущностями.
     *
     * @param e исключение
     * @return ответ с кодом 404 (Not Found)
     */
    @ExceptionHandler({
            UserNotFoundException.class,
            AdNotFoundException.class,
            CommentNotFoundException.class
    })
    public ResponseEntity<String> handleNotFoundException(RuntimeException e) {
        log.error("Сущность не найдена: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    /**
     * Обрабатывает исключение IllegalArgumentException (неверные параметры запроса).
     *
     * @param e исключение с описанием ошибки
     * @return ответ с HTTP 400 (Bad Request) и телом сообщения об ошибке
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Неверные данные: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    /**
     * Обрабатывает нарушение валидации параметров запроса.
     *
     * @param e исключение
     * @return ответ с кодом 400 (Bad Request)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Ошибка валидации параметров: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Некорректные параметры запроса");
    }

    /**
     * Обрабатывает все остальные исключения.
     *
     * @param e исключение
     * @return ответ с кодом 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Внутренняя ошибка сервера", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера");
    }
}
