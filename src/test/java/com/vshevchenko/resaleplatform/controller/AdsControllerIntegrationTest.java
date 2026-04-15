package com.vshevchenko.resaleplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class AdsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setPhone("+7(999)123-45-67");
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Test
    void getAllAds_WithoutAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void createAd_WithValidData_ShouldReturn201() throws Exception {
        CreateOrUpdateAd ad = new CreateOrUpdateAd();
        ad.setTitle("Новое объявление");
        ad.setPrice(1000);
        ad.setDescription("Описание объявления");

        // Создаем mock файл для картинки
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",  // имя параметра должно совпадать с @RequestPart("image")
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // Создаем mock для JSON части
        MockMultipartFile jsonPart = new MockMultipartFile(
                "properties",  // имя параметра должно совпадать с @RequestPart("properties")
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(ad)
        );

        mockMvc.perform(multipart("/ads")
                        .file(imageFile)
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("POST");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").exists())
                .andExpect(jsonPath("$.title").value("Новое объявление"))
                .andExpect(jsonPath("$.price").value(1000))
                .andExpect(jsonPath("$.image").exists());
    }

    @Test
    void getAdsMe_WithoutAuth_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    void getAdsMe_WithAuth_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").isNumber())
                .andExpect(jsonPath("$.results").isArray());
    }
}
