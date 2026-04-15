package com.vshevchenko.resaleplatform.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.vshevchenko.resaleplatform.dto.Role;
import com.vshevchenko.resaleplatform.dto.User;
import com.vshevchenko.resaleplatform.dto.Register;
import com.vshevchenko.resaleplatform.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper userMapper = new UserMapperImpl();

    private UserEntity userEntity;
    private User userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail("test@test.com");
        userEntity.setFirstName("Иван");
        userEntity.setLastName("Петров");
        userEntity.setPhone("+7(999)123-45-67");
        userEntity.setRole(Role.USER);
        userEntity.setImage("/users/1/image");

        userDto = new User();
        userDto.setId(1);
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Иван");
        userDto.setLastName("Петров");
        userDto.setPhone("+7(999)123-45-67");
        userDto.setRole(Role.USER);
        userDto.setImage("/users/1/image");
    }

    @Test
    void toUserDto_ShouldMapAllFields() {
        User result = userMapper.toUserDto(userEntity);

        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getFirstName(), result.getFirstName());
        assertEquals(userEntity.getLastName(), result.getLastName());
        assertEquals(userEntity.getPhone(), result.getPhone());
        assertEquals(userEntity.getRole(), result.getRole());
        assertEquals(userEntity.getImage(), result.getImage());
    }

    @Test
    void toUserEntity_ShouldMapAllFields() {
        UserEntity result = userMapper.toUserEntity(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        assertEquals(userDto.getPhone(), result.getPhone());
        assertEquals(userDto.getRole(), result.getRole());
        assertEquals(userDto.getImage(), result.getImage());
    }

    @Test
    void toUserEntityFromRegister_ShouldMapCorrectly() {
        Register register = new Register();
        register.setUsername("new@test.com");
        register.setFirstName("Новый");
        register.setLastName("Пользователь");
        register.setPhone("+7(999)111-22-33");
        register.setRole(Role.USER);

        UserEntity result = userMapper.toUserEntity(register);

        assertNotNull(result);
        assertEquals("new@test.com", result.getEmail());
        assertEquals("Новый", result.getFirstName());
        assertEquals("Пользователь", result.getLastName());
        assertEquals("+7(999)111-22-33", result.getPhone());
        assertEquals(Role.USER, result.getRole());
        assertNull(result.getId());
        assertNull(result.getImage());
        assertNull(result.getPassword());
    }
}
