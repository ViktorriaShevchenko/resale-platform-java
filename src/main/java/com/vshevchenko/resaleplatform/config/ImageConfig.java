package com.vshevchenko.resaleplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Конфигурация для работы с изображениями.
 * <p>
 * Создает и настраивает директорию для хранения загруженных изображений.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Configuration
public class ImageConfig {

    @Value("${upload.path:uploads}")
    private String uploadPath;

    /**
     * Создает и возвращает путь к директории для хранения изображений.
     * Если директория не существует, она будет создана.
     *
     * @return объект Path, указывающий на директорию для изображений
     * @throws IOException если не удается создать директорию
     */
    @Bean
    public Path imageStoragePath() throws IOException {
        Path path = Paths.get(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(path);
        return path;
    }
}
