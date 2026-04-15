package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.NewPassword;
import com.vshevchenko.resaleplatform.dto.UpdateUser;
import com.vshevchenko.resaleplatform.dto.User;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.exception.UserNotFoundException;
import com.vshevchenko.resaleplatform.mapper.UserMapper;
import com.vshevchenko.resaleplatform.repository.UserRepository;

/**
 * Реализация сервиса для работы с пользователями.
 * Содержит бизнес-логику получения информации о пользователе,
 * обновления профиля, смены пароля и обновления аватара.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService; // НОВОЕ

    /**
     * Получает информацию о пользователе по его email.
     *
     * @param email email пользователя
     * @return объект User с данными пользователя
     * @throws UserNotFoundException если пользователь с таким email не найден
     */
    @Override
    public User getUser(String email) {
        log.info("Получение информации о пользователе с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return userMapper.toUserDto(userEntity);
    }

    /**
     * Обновляет информацию о пользователе (имя, фамилию, телефон).
     *
     * @param email email пользователя, чьи данные обновляются
     * @param updateUser DTO с новыми данными
     * @return обновленные данные пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public UpdateUser updateUser(String email, UpdateUser updateUser) {
        log.info("Обновление информации о пользователе с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        userMapper.updateUserEntityFromDto(updateUser, userEntity);
        userRepository.save(userEntity);

        return updateUser;
    }

    /**
     * Обновляет пароль пользователя.
     * Проверяет, что старый пароль введен верно, и шифрует новый.
     *
     * @param email email пользователя
     * @param newPassword DTO с текущим и новым паролем
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если текущий пароль неверен
     */
    @Override
    @Transactional
    public void updatePassword(String email, NewPassword newPassword) {
        log.info("Обновление пароля для пользователя с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        if (!passwordEncoder.matches(newPassword.getCurrentPassword(), userEntity.getPassword())) {
            throw new AccessDeniedException("Неверный текущий пароль");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        userRepository.save(userEntity);

        log.info("Пароль успешно обновлен для пользователя: {}", email);
    }

    /**
     * Обновляет аватар пользователя.
     * Старое изображение удаляется, новое сохраняется.
     *
     * @param email email пользователя
     * @param image новый файл аватара
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public void updateUserImage(String email, MultipartFile image) {
        log.info("Обновление аватара для пользователя с email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Обновляем изображение (старое удалится автоматически)
        String newImagePath = imageService.updateImage(userEntity.getImage(), image, "user", userEntity.getId());
        userEntity.setImage(newImagePath);
        userRepository.save(userEntity);
    }
}
