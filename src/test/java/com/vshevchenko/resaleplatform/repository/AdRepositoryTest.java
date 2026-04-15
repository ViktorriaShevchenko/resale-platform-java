package com.vshevchenko.resaleplatform.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import com.vshevchenko.resaleplatform.entity.AdEntity;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.dto.Role;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class AdRepositoryTest {

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setEmail("author@test.com");
        user.setPassword("password");
        user.setFirstName("Автор");
        user.setLastName("Тестов");
        user.setPhone("+7(999)111-22-33");
        user.setRole(Role.USER);
        user = userRepository.save(user);
    }

    @Test
    void saveAd_ShouldPersistAllFields() {
        AdEntity ad = new AdEntity();
        ad.setTitle("Тестовое объявление");
        ad.setDescription("Описание тестового объявления");
        ad.setPrice(1000);
        ad.setAuthor(user);
        ad.setImage("/ads/1/image");

        AdEntity savedAd = adRepository.save(ad);

        assertNotNull(savedAd.getPk());
        assertEquals("Тестовое объявление", savedAd.getTitle());
        assertEquals("Описание тестового объявления", savedAd.getDescription());
        assertEquals(1000, savedAd.getPrice());
        assertEquals(user.getId(), savedAd.getAuthor().getId());
    }

    @Test
    void findByAuthorId_ShouldReturnUserAds() {
        AdEntity ad1 = new AdEntity();
        ad1.setTitle("Объявление 1");
        ad1.setDescription("Описание 1");
        ad1.setPrice(1000);
        ad1.setAuthor(user);
        adRepository.save(ad1);

        AdEntity ad2 = new AdEntity();
        ad2.setTitle("Объявление 2");
        ad2.setDescription("Описание 2");
        ad2.setPrice(2000);
        ad2.setAuthor(user);
        adRepository.save(ad2);

        List<AdEntity> userAds = adRepository.findByAuthorId(user.getId());

        assertEquals(2, userAds.size());
        assertTrue(userAds.stream().allMatch(ad -> ad.getAuthor().getId().equals(user.getId())));
    }

    @Test
    void deleteAd_ShouldRemoveFromDatabase() {
        AdEntity ad = new AdEntity();
        ad.setTitle("Объявление для удаления");
        ad.setDescription("Будет удалено");
        ad.setPrice(500);
        ad.setAuthor(user);
        ad = adRepository.save(ad);

        adRepository.deleteById(ad.getPk());

        Optional<AdEntity> deletedAd = adRepository.findById(ad.getPk());
        assertTrue(deletedAd.isEmpty());
    }
}
