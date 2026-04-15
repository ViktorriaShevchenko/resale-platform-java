package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.repository.UserRepository;

/**
 * Реализация UserDetailsService для загрузки данных пользователя из базы данных.
 * Используется Spring Security для аутентификации.
 *
 * Этот сервис загружает пользователя по email (username) из БД и преобразует
 * его в объект UserDetails, который использует Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Загружает пользователя по его email (username) из базы данных.
     * Преобразует сущность UserEntity в объект UserDetails для Spring Security.
     *
     * @param username email пользователя
     * @return объект UserDetails с данными пользователя (email, пароль, роль)
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя в БД по email
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        // Создаем объект UserDetails для Spring Security
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
