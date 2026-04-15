package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.repository.UserRepository;

/**
 * Кастомная реализация UserDetailsManager для управления пользователями.
 * Расширяет функциональность UserDetailsService, добавляя методы
 * для создания, обновления и удаления пользователей.
 *
 * Используется для полного управления жизненным циклом пользователей в системе.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsManager implements UserDetailsManager {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Создает нового пользователя в системе.
     *
     * @param user данные нового пользователя
     * @throws UnsupportedOperationException всегда, так как создание пользователей
     *         должно выполняться через AuthService.register для соблюдения бизнес-логики
     */
    @Override
    public void createUser(UserDetails user) {
        // Пользователи создаются через регистрацию, этот метод может быть пустым
        throw new UnsupportedOperationException("Используйте AuthService.register для создания пользователей");
    }

    /**
     * Обновляет существующего пользователя.
     * Используется для обновления пароля и других данных пользователя.
     *
     * @param user обновленные данные пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public void updateUser(UserDetails user) {
        // Обновляем только если пользователь существует
        UserEntity userEntity = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Обновляем пароль (остальные данные обновляются через UserService)
        userEntity.setPassword(user.getPassword());
        userRepository.save(userEntity);
    }

    /**
     * Удаляет пользователя из системы.
     *
     * @param username email пользователя для удаления
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
        userRepository.delete(userEntity);
    }

    /**
     * Изменяет пароль пользователя.
     * Этот метод не используется, так как смена пароля реализована через UserService.
     *
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @throws UnsupportedOperationException всегда, так как смена пароля
     *         должна выполняться через UserService.updatePassword
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // Этот метод не используется, т.к. мы работаем через UserService
        throw new UnsupportedOperationException("Используйте UserService.updatePassword");
    }

    /**
     * Проверяет, существует ли пользователь с указанным email.
     *
     * @param username email пользователя
     * @return true если пользователь существует, false если нет
     */
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    /**
     * Загружает пользователя по его email.
     * Делегирует выполнение CustomUserDetailsService.
     *
     * @param username email пользователя
     * @return объект UserDetails с данными пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsService.loadUserByUsername(username);
    }
}
