package com.vshevchenko.resaleplatform.service;

import com.vshevchenko.resaleplatform.dto.Ads;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.Role;
import com.vshevchenko.resaleplatform.entity.AdEntity;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.exception.AdNotFoundException;
import com.vshevchenko.resaleplatform.exception.UserNotFoundException;
import com.vshevchenko.resaleplatform.mapper.AdMapper;
import com.vshevchenko.resaleplatform.repository.AdRepository;
import com.vshevchenko.resaleplatform.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdMapper adMapper;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private AdServiceImpl adService;

    private UserEntity author;
    private AdEntity adEntity;
    private CreateOrUpdateAd createOrUpdateAd;

    @BeforeEach
    void setUp() {
        author = new UserEntity();
        author.setId(1);
        author.setEmail("author@test.com");
        author.setRole(Role.USER);

        adEntity = new AdEntity();
        adEntity.setPk(100);
        adEntity.setTitle("Тестовое объявление");
        adEntity.setPrice(1000);
        adEntity.setDescription("Старое описание");
        adEntity.setImage("/ads/old_image.jpg");
        adEntity.setAuthor(author);

        createOrUpdateAd = new CreateOrUpdateAd();
        createOrUpdateAd.setTitle("Новое название");
        createOrUpdateAd.setPrice(2000);
        createOrUpdateAd.setDescription("Новое описание");
    }

    @Test
    void updateAd_WhenUserIsAuthor_ShouldUpdate() {
        // Arrange
        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));

        Ad updatedAdDto = new Ad();
        updatedAdDto.setPk(100);
        updatedAdDto.setTitle("Новое название");
        updatedAdDto.setPrice(2000);
        updatedAdDto.setAuthor(1);

        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(updatedAdDto);

        // Act
        Ad result = adService.updateAd(100, "author@test.com", createOrUpdateAd);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getPk());
        assertEquals("Новое название", result.getTitle());
        assertEquals(2000, result.getPrice());

        verify(adRepository).findById(100);
        verify(adMapper).updateAdEntityFromDto(eq(createOrUpdateAd), any(AdEntity.class));
        verify(adRepository).save(any(AdEntity.class));
        verify(adMapper).toAdDto(any(AdEntity.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void updateAd_WhenUserIsAdmin_ShouldUpdate() {
        // Arrange
        UserEntity admin = new UserEntity();
        admin.setId(2);
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));

        Ad updatedAdDto = new Ad();
        updatedAdDto.setPk(100);
        updatedAdDto.setTitle("Новое название");
        updatedAdDto.setPrice(2000);
        updatedAdDto.setAuthor(1);

        when(adRepository.save(any(AdEntity.class))).thenReturn(adEntity);
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(updatedAdDto);

        // Act
        Ad result = adService.updateAd(100, "admin@test.com", createOrUpdateAd);

        // Assert
        assertNotNull(result);
        verify(adRepository).findById(100);
        verify(userRepository).findByEmail("admin@test.com");
    }

    @Test
    void updateAd_WhenUserIsNotAuthorAndNotAdmin_ShouldThrowAccessDenied() {
        // Arrange
        UserEntity otherUser = new UserEntity();
        otherUser.setId(3);
        otherUser.setEmail("other@test.com");
        otherUser.setRole(Role.USER);

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> adService.updateAd(100, "other@test.com", createOrUpdateAd));

        verify(adRepository).findById(100);
        verify(userRepository).findByEmail("other@test.com");
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_WhenAdNotFound_ShouldThrowAdNotFoundException() {
        // Arrange
        when(adRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AdNotFoundException.class,
                () -> adService.updateAd(999, "author@test.com", createOrUpdateAd));

        verify(adRepository).findById(999);
        verify(userRepository, never()).findByEmail(anyString());
        verify(adRepository, never()).save(any());
    }

    @Test
    void updateAd_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Arrange
        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> adService.updateAd(100, "unknown@test.com", createOrUpdateAd));
    }

    @Test
    void createAd_WithImage_ShouldSaveImage() {
        // Arrange
        String email = "author@test.com";
        MultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        AdEntity newAdEntity = new AdEntity();  // Создаем новую сущность, не используем adEntity из setUp
        newAdEntity.setAuthor(author);

        AdEntity savedAd = new AdEntity();
        savedAd.setPk(100);
        savedAd.setAuthor(author);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(adMapper.toAdEntity(createOrUpdateAd)).thenReturn(newAdEntity);  // ВАЖНО: мокаем создание сущности
        when(adRepository.save(any(AdEntity.class))).thenReturn(savedAd);
        when(imageService.saveImage(any(), eq("ad"), eq(100))).thenReturn("/images/ad_100_test.jpg");

        Ad expectedAdDto = new Ad();
        expectedAdDto.setPk(100);
        expectedAdDto.setTitle("Новое название");
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(expectedAdDto);

        // Act
        Ad result = adService.createAd(email, createOrUpdateAd, image);

        // Assert
        assertNotNull(result);
        verify(adMapper).toAdEntity(createOrUpdateAd);
        verify(adRepository, times(2)).save(any(AdEntity.class));
        verify(imageService).saveImage(eq(image), eq("ad"), eq(100));
        verify(adMapper).toAdDto(any(AdEntity.class));
    }

    // ИСПРАВЛЕННЫЙ ТЕСТ: создание объявления без картинки
    @Test
    void createAd_WithoutImage_ShouldNotCallImageService() {
        // Arrange
        String email = "author@test.com";

        AdEntity newAdEntity = new AdEntity();  // Создаем новую сущность
        newAdEntity.setAuthor(author);

        AdEntity savedAd = new AdEntity();
        savedAd.setPk(100);
        savedAd.setAuthor(author);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(author));
        when(adMapper.toAdEntity(createOrUpdateAd)).thenReturn(newAdEntity);  // ВАЖНО: мокаем создание сущности
        when(adRepository.save(any(AdEntity.class))).thenReturn(savedAd);

        Ad expectedAdDto = new Ad();
        expectedAdDto.setPk(100);
        when(adMapper.toAdDto(any(AdEntity.class))).thenReturn(expectedAdDto);

        // Act
        Ad result = adService.createAd(email, createOrUpdateAd, null);

        // Assert
        assertNotNull(result);
        verify(adMapper).toAdEntity(createOrUpdateAd);
        verify(adRepository, times(1)).save(any(AdEntity.class));
        verify(imageService, never()).saveImage(any(), any(), any());
    }

    @Test
    void deleteAd_WithImage_ShouldDeleteImage() {
        // Arrange
        String email = "author@test.com";

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));

        // Act
        adService.deleteAd(100, email);

        // Assert
        verify(adRepository).findById(100);
        verify(imageService).deleteImage("/ads/old_image.jpg");
        verify(adRepository).delete(adEntity);
    }

    // ИСПРАВЛЕННЫЙ ТЕСТ: удаление объявления без картинки
    @Test
    void deleteAd_WithoutImage_ShouldNotCallImageService() {
        // Arrange
        String email = "author@test.com";
        adEntity.setImage(null);

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));

        // Act
        adService.deleteAd(100, email);

        // Assert
        verify(imageService, never()).deleteImage(any());
        verify(adRepository).delete(adEntity);
    }

    @Test
    void updateAdImage_ShouldUpdateImage() {
        // Arrange
        String email = "author@test.com";
        MultipartFile newImage = new MockMultipartFile(
                "image",
                "new.jpg",
                "image/jpeg",
                "new image content".getBytes()
        );

        when(adRepository.findById(100)).thenReturn(Optional.of(adEntity));
        when(imageService.updateImage(adEntity.getImage(), newImage, "ad", 100))
                .thenReturn("/images/ad_100_new.jpg");

        // Act
        adService.updateAdImage(100, email, newImage);

        // Assert
        verify(adRepository).findById(100);
        verify(imageService).updateImage("/ads/old_image.jpg", newImage, "ad", 100);
        verify(adRepository).save(adEntity);
        assertEquals("/images/ad_100_new.jpg", adEntity.getImage());
    }

    @Test
    void getAllAdsPaged_ShouldReturnPagedAds() {
        // Arrange
        AdEntity secondAdEntity = new AdEntity();
        secondAdEntity.setPk(101);
        secondAdEntity.setTitle("Второе объявление");
        secondAdEntity.setPrice(1500);
        secondAdEntity.setDescription("Описание второго объявления");
        secondAdEntity.setAuthor(author);

        Ad firstAdDto = new Ad();
        firstAdDto.setPk(100);
        firstAdDto.setTitle("Тестовое объявление");
        firstAdDto.setPrice(1000);
        firstAdDto.setAuthor(1);

        Ad secondAdDto = new Ad();
        secondAdDto.setPk(101);
        secondAdDto.setTitle("Второе объявление");
        secondAdDto.setPrice(1500);
        secondAdDto.setAuthor(1);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("pk").ascending());
        Page<AdEntity> page = new PageImpl<>(List.of(adEntity, secondAdEntity), pageable, 5);

        when(adRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(adMapper.toAdDtoList(List.of(adEntity, secondAdEntity)))
                .thenReturn(List.of(firstAdDto, secondAdDto));

        // Act
        Ads result = adService.getAllAdsPaged(0, 2, "pk", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getCount());
        assertEquals(2, result.getResults().size());
        assertEquals(100, result.getResults().get(0).getPk());
        assertEquals(101, result.getResults().get(1).getPk());

        verify(adRepository).findAll(any(Pageable.class));
        verify(adMapper).toAdDtoList(List.of(adEntity, secondAdEntity));
    }

    @Test
    void getAllAdsPaged_ShouldPassCorrectPageableToRepository() {
        // Arrange
        Page<AdEntity> page = new PageImpl<>(List.of(adEntity));
        Ad adDto = new Ad();
        adDto.setPk(100);
        adDto.setTitle("Тестовое объявление");
        adDto.setPrice(1000);

        when(adRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(adMapper.toAdDtoList(anyList())).thenReturn(List.of(adDto));

        // Act
        adService.getAllAdsPaged(1, 3, "price", "desc");

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(adRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(1, capturedPageable.getPageNumber());
        assertEquals(3, capturedPageable.getPageSize());

        Sort.Order order = capturedPageable.getSort().getOrderFor("price");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void getAllAdsPaged_WithUnsupportedSortField_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adService.getAllAdsPaged(0, 5, "author", "asc")
        );

        assertEquals("Неподдерживаемое поле сортировки: author", exception.getMessage());
        verify(adRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllAdsPaged_WithUnsupportedDirection_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> adService.getAllAdsPaged(0, 5, "pk", "down")
        );

        assertEquals("Неподдерживаемое направление сортировки: down", exception.getMessage());
        verify(adRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllAdsPaged_WhenPageIsEmpty_ShouldReturnEmptyResults() {
        // Arrange
        Pageable pageable = PageRequest.of(10, 5, Sort.by("pk").ascending());
        Page<AdEntity> emptyPage = new PageImpl<>(List.of(), pageable, 2);

        when(adRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
        when(adMapper.toAdDtoList(List.of())).thenReturn(List.of());

        // Act
        Ads result = adService.getAllAdsPaged(10, 5, "pk", "asc");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getCount());
        assertTrue(result.getResults().isEmpty());

        verify(adRepository).findAll(any(Pageable.class));
        verify(adMapper).toAdDtoList(List.of());
    }
}
