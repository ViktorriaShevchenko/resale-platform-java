package com.vshevchenko.resaleplatform.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public UserDetailsManager testUserDetailsManager() {
        UserDetails user = User.builder()
                .username("user@test.com")
                .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin@test.com")
                .password("{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public Path testImageStoragePath() throws IOException {
        // Создаем временную директорию для тестовых изображений
        Path tempDir = Files.createTempDirectory("test-uploads");
        tempDir.toFile().deleteOnExit(); // директория удалится после завершения JVM
        return tempDir;
    }
}