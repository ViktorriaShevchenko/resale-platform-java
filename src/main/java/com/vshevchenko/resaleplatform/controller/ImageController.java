package com.vshevchenko.resaleplatform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Контроллер для получения изображений.
 * <p>
 * Предоставляет эндпоинт для доступа к загруженным изображениям
 * (аватары пользователей и картинки объявлений).
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final Path imageStoragePath;

    /**
     * Получает изображение по имени файла.
     * <p>
     * Выполняет проверку на path traversal атаки и возвращает изображение
     * с соответствующим Content-Type.
     * </p>
     *
     * @param filename имя файла изображения
     * @return массив байтов изображения с кодом 200, или 404 если файл не найден,
     *         или 403 при попытке path traversal
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            // Защита от path traversal атак
            Path imagePath = imageStoragePath.resolve(filename).normalize();

            if (!imagePath.startsWith(imageStoragePath)) {
                log.warn("Попытка доступа к файлу за пределами директории: {}", filename);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            if (!Files.exists(imagePath)) {
                log.warn("Изображение не найдено: {}", filename);
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);

            // Определяем content type по расширению
            String contentType = getContentType(filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);

        } catch (IOException e) {
            log.error("Ошибка при чтении изображения: {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Определяет MIME-тип изображения по расширению файла.
     *
     * @param filename имя файла
     * @return MIME-тип (image/png, image/gif, image/bmp, image/jpeg)
     */
    private String getContentType(String filename) {
        if (filename.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (filename.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (filename.toLowerCase().endsWith(".bmp")) {
            return "image/bmp";
        } else {
            return "image/jpeg"; // по умолчанию для .jpg, .jpeg
        }
    }
}
