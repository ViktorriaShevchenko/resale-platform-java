package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Сервис для работы с изображениями.
 * Предоставляет функционал сохранения, удаления и обновления файлов изображений
 * для объявлений и аватаров пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final Path imageStoragePath;

    /**
     * Сохраняет изображение на диск и возвращает URL для доступа к нему.
     * Выполняет валидацию файла (не пустой, тип image/*, размер не более 5MB).
     * Генерирует уникальное имя файла для предотвращения конфликтов.
     *
     * @param file файл изображения, загруженный пользователем
     * @param entityType тип сущности (например, "ad" для объявления или "user" для аватара)
     * @param entityId идентификатор сущности, к которой относится изображение
     * @return URL для доступа к изображению (например, "/images/ad_123_abc123.jpg")
     * @throws IllegalArgumentException если файл пустой, не является изображением или слишком большой
     * @throws RuntimeException если произошла ошибка при сохранении файла
     */
    public String saveImage(MultipartFile file, String entityType, Integer entityId) {
        try {
            // 1. Валидация файла
            validateImage(file);

            // 2. Генерация уникального имени файла
            String filename = generateFilename(file, entityType, entityId);

            // 3. Сохранение файла
            Path targetPath = imageStoragePath.resolve(filename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Возвращаем URL для доступа к изображению
            return "/images/" + filename;

        } catch (IOException e) {
            log.error("Ошибка при сохранении изображения", e);
            throw new RuntimeException("Ошибка при сохранении изображения", e);
        }
    }

    /**
     * Удаляет изображение с диска по его URL.
     * Если URL пустой или файл не существует, метод ничего не делает.
     *
     * @param imageUrl URL изображения для удаления (например, "/images/ad_123_abc123.jpg")
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // Извлекаем имя файла из URL
            String filename = extractFilenameFromUrl(imageUrl);
            Path filePath = imageStoragePath.resolve(filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Удалено изображение: {}", filename);
            }
        } catch (IOException e) {
            log.error("Ошибка при удалении изображения: {}", imageUrl, e);
        }
    }

    /**
     * Обновляет изображение - удаляет старое и сохраняет новое.
     * Если старое изображение существовало, оно будет удалено.
     * Если старое изображение отсутствовало (null), просто сохранит новое.
     *
     * @param oldImageUrl URL старого изображения (может быть null)
     * @param newFile новый файл изображения
     * @param entityType тип сущности
     * @param entityId идентификатор сущности
     * @return URL нового изображения
     */
    public String updateImage(String oldImageUrl, MultipartFile newFile,
                              String entityType, Integer entityId) {
        // Удаляем старое изображение, если оно есть
        deleteImage(oldImageUrl);

        // Сохраняем новое
        return saveImage(newFile, entityType, entityId);
    }

    /**
     * Проверяет корректность загружаемого файла.
     *
     * @param file файл для проверки
     * @throws IllegalArgumentException если файл пустой, не является изображением или слишком большой
     */
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Разрешены только изображения");
        }

        if (file.getSize() > 5 * 1024 * 1024) { // 5 MB
            throw new IllegalArgumentException("Размер файла не может превышать 5MB");
        }
    }

    /**
     * Генерирует уникальное имя файла.
     * Формат: тип_entityId_уникальный_идентификатор.расширение
     *
     * @param file исходный файл
     * @param entityType тип сущности
     * @param entityId идентификатор сущности
     * @return сгенерированное имя файла
     */
    private String generateFilename(MultipartFile file, String entityType, Integer entityId) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        return String.format("%s_%s.%s",
                entityType,
                UUID.randomUUID().toString().substring(0, 8),
                extension);
    }

    /**
     * Извлекает расширение файла из имени.
     *
     * @param filename полное имя файла
     * @return расширение файла (без точки), по умолчанию "jpg"
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Извлекает имя файла из URL.
     *
     * @param imageUrl URL изображения (например, "/images/ad_123_abc123.jpg")
     * @return имя файла (например, "ad_123_abc123.jpg")
     */
    private String extractFilenameFromUrl(String imageUrl) {
        // URL формата: /images/filename.jpg
        if (imageUrl.startsWith("/images/")) {
            return imageUrl.substring(8); // Убираем "/images/"
        }
        return imageUrl;
    }
}
