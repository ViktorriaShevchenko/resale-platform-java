package com.vshevchenko.resaleplatform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.vshevchenko.resaleplatform.config.TestConfig;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.Role;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.repository.UserRepository;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class ImageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Path imageStoragePath;

    @Value("${upload.path}")
    private String uploadPath;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setPhone("+7(999)123-45-67");
        user.setRole(Role.USER);
        userRepository.save(user);

        // Очищаем тестовую директорию
        if (Files.exists(imageStoragePath)) {
            Files.list(imageStoragePath).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (Exception e) {
                    // игнорируем
                }
            });
        }
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void getImage_WithValidFilename_ShouldReturnImage() throws Exception {
        // Сначала создадим объявление с картинкой, чтобы файл появился
        CreateOrUpdateAd ad = new CreateOrUpdateAd();
        ad.setTitle("Тест");
        ad.setPrice(1000);
        ad.setDescription("Описание");

        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        MockMultipartFile jsonPart = new MockMultipartFile(
                "properties",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsBytes(ad)
        );

        // Создаем объявление
        String response = mockMvc.perform(multipart("/ads")
                        .file(imageFile)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Извлекаем имя файла из ответа
        String imageUrl = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("image")
                .asText();

        String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);

        // Теперь тестируем получение картинки
        mockMvc.perform(get("/images/{filename}", filename))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser
    void getImage_WithNonExistentFile_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/images/nonexistent.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getImage_WithPathTraversal_ShouldBeRejectedBySecurity() {
        assertThrows(org.springframework.security.web.firewall.RequestRejectedException.class, () -> {
            mockMvc.perform(get("/images/../../../etc/passwd"));
        });
    }
}
