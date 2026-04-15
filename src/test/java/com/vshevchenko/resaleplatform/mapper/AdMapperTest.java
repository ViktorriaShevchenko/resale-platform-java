package com.vshevchenko.resaleplatform.mapper;

import org.junit.jupiter.api.Test;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.entity.AdEntity;
import com.vshevchenko.resaleplatform.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdMapperTest {

    private AdMapper adMapper = new AdMapperImpl();

    @Test
    void testAdToDtoMapping() {
        // Создаем тестового пользователя
        UserEntity author = new UserEntity();
        author.setId(1);
        author.setEmail("test@test.com");
        author.setFirstName("Иван");

        // Создаем тестовое объявление
        AdEntity adEntity = new AdEntity();
        adEntity.setPk(100);
        adEntity.setTitle("Тест");
        adEntity.setPrice(1000);
        adEntity.setAuthor(author);

        // Маппим в DTO
        Ad adDto = adMapper.toAdDto(adEntity);

        // Проверяем
        assertNotNull(adDto);
        assertEquals(100, adDto.getPk());
        assertEquals("Тест", adDto.getTitle());
        assertEquals(1000, adDto.getPrice());
        assertEquals(1, adDto.getAuthor());
    }
}
