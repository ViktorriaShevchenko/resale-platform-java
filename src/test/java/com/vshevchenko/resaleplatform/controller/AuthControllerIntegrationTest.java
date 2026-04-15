package com.vshevchenko.resaleplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.vshevchenko.resaleplatform.config.TestConfig;
import com.vshevchenko.resaleplatform.dto.Login;
import com.vshevchenko.resaleplatform.dto.Register;
import com.vshevchenko.resaleplatform.dto.Role;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setEmail("user@test.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Иван");
        user.setLastName("Петров");
        user.setPhone("+7(999)123-45-67");
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Test
    void register_WithValidData_ShouldReturn201() throws Exception {
        Register register = new Register();
        register.setUsername("newuser@test.com");
        register.setPassword("password123");
        register.setFirstName("Новый");
        register.setLastName("Пользователь");
        register.setPhone("+7(999)111-22-33");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());
    }

    @Test
    void register_WithExistingUser_ShouldReturn400() throws Exception {
        Register register = new Register();
        register.setUsername("user@test.com"); // уже существует
        register.setPassword("password123");
        register.setFirstName("Новый");
        register.setLastName("Пользователь");
        register.setPhone("+7(999)111-22-33");
        register.setRole(Role.USER);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithValidCredentials_ShouldReturn200() throws Exception {
        Login login = new Login();
        login.setUsername("user@test.com");
        login.setPassword("password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andDo(result -> {

                    System.out.println("Response status: " + result.getResponse().getStatus());
                    System.out.println("Response body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());
    }

    @Test
    void login_WithInvalidPassword_ShouldReturn401() throws Exception {
        Login login = new Login();
        login.setUsername("user@test.com");
        login.setPassword("wrongpassword");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithNonExistentUser_ShouldReturn401() throws Exception {
        Login login = new Login();
        login.setUsername("nonexistent@test.com");
        login.setPassword("password");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
