package com.vshevchenko.resaleplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        imageService = new ImageService(tempDir);
    }

    @Test
    void saveImage_ShouldSaveFileAndReturnUrl() {
        // given
        MultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // when
        String imageUrl = imageService.saveImage(file, "ad", 1);

        // then
        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("/images/"));
        assertTrue(Files.exists(tempDir.resolve(imageUrl.substring(8))));
    }

    @Test
    void saveImage_WithEmptyFile_ShouldThrowException() {
        // given
        MultipartFile file = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when/then
        assertThrows(IllegalArgumentException.class,
                () -> imageService.saveImage(file, "ad", 1));
    }

    @Test
    void saveImage_WithNonImageFile_ShouldThrowException() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "text content".getBytes()
        );

        // when/then
        assertThrows(IllegalArgumentException.class,
                () -> imageService.saveImage(file, "ad", 1));
    }

    @Test
    void deleteImage_ShouldRemoveFile() {
        // given
        MultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
        String imageUrl = imageService.saveImage(file, "ad", 1);
        Path savedFile = tempDir.resolve(imageUrl.substring(8));
        assertTrue(Files.exists(savedFile));

        // when
        imageService.deleteImage(imageUrl);

        // then
        assertFalse(Files.exists(savedFile));
    }

    @Test
    void updateImage_ShouldDeleteOldAndSaveNew() {
        // given
        MultipartFile oldFile = new MockMultipartFile(
                "image",
                "old.jpg",
                "image/jpeg",
                "old content".getBytes()
        );
        String oldUrl = imageService.saveImage(oldFile, "ad", 1);
        Path oldPath = tempDir.resolve(oldUrl.substring(8));

        MultipartFile newFile = new MockMultipartFile(
                "image",
                "new.jpg",
                "image/jpeg",
                "new content".getBytes()
        );

        // when
        String newUrl = imageService.updateImage(oldUrl, newFile, "ad", 1);

        // then
        assertFalse(Files.exists(oldPath));
        assertTrue(Files.exists(tempDir.resolve(newUrl.substring(8))));
        assertNotEquals(oldUrl, newUrl);
    }
}
