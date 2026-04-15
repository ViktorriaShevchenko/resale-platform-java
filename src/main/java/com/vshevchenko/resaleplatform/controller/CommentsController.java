package com.vshevchenko.resaleplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.vshevchenko.resaleplatform.dto.Comment;
import com.vshevchenko.resaleplatform.dto.Comments;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateComment;
import com.vshevchenko.resaleplatform.service.CommentService;

/**
 * Контроллер для управления комментариями к объявлениям.
 * <p>
 * Предоставляет REST API для получения, добавления, обновления и удаления комментариев.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@Tag(name = "Комментарии")
public class CommentsController {

    private final CommentService commentService;

    /**
     * Получает все комментарии к объявлению.
     * <p>
     * Доступно без аутентификации.
     * </p>
     *
     * @param id идентификатор объявления
     * @return список комментариев
     */
    @Operation(
            summary = "Получение комментариев объявления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/ads/{id}/comments")
    public ResponseEntity<Comments> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.getCommentsByAdId(id));
    }

    /**
     * Добавляет новый комментарий к объявлению.
     * <p>
     * Доступно только авторизованным пользователям.
     * </p>
     *
     * @param id идентификатор объявления
     * @param comment данные комментария
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return созданный комментарий
     */
    @Operation(
            summary = "Добавление комментария к объявлению",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PostMapping("/ads/{id}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody CreateOrUpdateComment comment,
                                              Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(id, authentication.getName(), comment));
    }

    /**
     * Удаляет комментарий.
     * <p>
     * Доступно только автору комментария или администратору.
     * </p>
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return пустой ответ с кодом 200
     */
    @Operation(
            summary = "Удаление комментария",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @DeleteMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId,
                                              Authentication authentication) {
        commentService.deleteComment(adId, commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Обновляет существующий комментарий.
     * <p>
     * Доступно только автору комментария или администратору.
     * </p>
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param comment новые данные комментария
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return обновленный комментарий
     */
    @Operation(
            summary = "Обновление комментария",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping("/ads/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId,
                                                 @RequestBody CreateOrUpdateComment comment,
                                                 Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(adId, commentId,
                authentication.getName(), comment));
    }
}
