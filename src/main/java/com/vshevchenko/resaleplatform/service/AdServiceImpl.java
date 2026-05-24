package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.dto.Ads;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.ExtendedAd;
import com.vshevchenko.resaleplatform.entity.AdEntity;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.exception.AdNotFoundException;
import com.vshevchenko.resaleplatform.exception.UserNotFoundException;
import com.vshevchenko.resaleplatform.mapper.AdMapper;
import com.vshevchenko.resaleplatform.repository.AdRepository;
import com.vshevchenko.resaleplatform.repository.UserRepository;

import java.util.List;
import java.util.Set;

/**
 * Реализация сервиса для работы с объявлениями.
 * Содержит бизнес-логику создания, обновления, удаления и получения объявлений.
 * Выполняет проверку прав доступа к операциям с объявлениями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("pk", "price", "title");

    /**
     * Получает список всех объявлений.
     *
     * @return объект Ads, содержащий количество объявлений и их список
     */
    @Override
    public Ads getAllAds() {
        log.info("Получение всех объявлений");

        List<AdEntity> entities = adRepository.findAll();
        List<Ad> adList = adMapper.toAdDtoList(entities);

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }

    /**
     * Получает список всех объявлений с поддержкой пагинации и сортировки.
     *
     * @param page номер страницы, начиная с 0
     * @param size количество элементов на странице
     * @param sortBy поле для сортировки
     * @param direction направление сортировки: asc или desc
     * @return объект Ads, содержащий общее количество объявлений и результаты текущей страницы
     */
    @Override
    public Ads getAllAdsPaged(int page, int size, String sortBy, String direction) {
        log.info("Получение объявлений с пагинацией. page: {}, size: {}, sortBy: {}, direction: {}",
                page, size, sortBy, direction);

        validateSortField(sortBy);
        validateDirection(direction);

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdEntity> entityPage = adRepository.findAll(pageable);

        List<Ad> adList = adMapper.toAdDtoList(entityPage.getContent());

        Ads ads = new Ads();
        ads.setCount((int) entityPage.getTotalElements());
        ads.setResults(adList);

        return ads;
    }

    /**
     * Проверяет, разрешено ли указанное поле для сортировки.
     *
     * @param sortBy имя поля для проверки
     * @throws IllegalArgumentException если поле sortBy отсутствует в списке разрешённых
     */
    private void validateSortField(String sortBy) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Неподдерживаемое поле сортировки: " + sortBy);
        }
    }

    /**
     * Проверяет корректность направления сортировки.
     *
     * @param direction направление сортировки (допустимые значения: asc или desc)
     * @throws IllegalArgumentException если direction не равен asc или desc
     */
    private void validateDirection(String direction) {
        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            throw new IllegalArgumentException("Неподдерживаемое направление сортировки: " + direction);
        }
    }

    /**
     * Получает все объявления конкретного пользователя.
     *
     * @param email email пользователя, чьи объявления нужно получить
     * @return объект Ads с объявлениями пользователя
     * @throws UserNotFoundException если пользователь с таким email не найден
     */
    @Override
    public Ads getAdsByUser(String email) {
        log.info("Получение объявлений пользователя с email: {}", email);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<AdEntity> entities = adRepository.findByAuthorId(user.getId());
        List<Ad> adList = adMapper.toAdDtoList(entities);

        Ads ads = new Ads();
        ads.setCount(adList.size());
        ads.setResults(adList);

        return ads;
    }

    /**
     * Получает детальную информацию об объявлении по его ID.
     *
     * @param id идентификатор объявления
     * @return расширенная информация об объявлении
     * @throws AdNotFoundException если объявление с таким ID не найдено
     */
    @Override
    public ExtendedAd getAdById(Integer id) {
        log.info("Получение объявления с id: {}", id);

        AdEntity entity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        return adMapper.toExtendedAdDto(entity);
    }

    /**
     * Создает новое объявление.
     * Сначала сохраняет объявление без картинки, затем добавляет картинку.
     *
     * @param email email автора объявления
     * @param createOrUpdateAd DTO с данными объявления (название, цена, описание)
     * @param image файл изображения для объявления
     * @return созданное объявление в формате DTO
     * @throws UserNotFoundException если автор не найден
     * @throws IllegalArgumentException если файл изображения некорректный
     */
    @Override
    @Transactional
    public Ad createAd(String email, CreateOrUpdateAd createOrUpdateAd, MultipartFile image) {
        log.info("Создание нового объявления пользователем с email: {}", email);

        UserEntity author = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        AdEntity adEntity = adMapper.toAdEntity(createOrUpdateAd);
        adEntity.setAuthor(author);

        // Сохраняем сначала без изображения, чтобы получить id
        AdEntity savedAd = adRepository.save(adEntity);

        // Теперь сохраняем изображение с правильным id
        if (image != null && !image.isEmpty()) {
            String imagePath = imageService.saveImage(image, "ad", savedAd.getPk());
            savedAd.setImage(imagePath);
            savedAd = adRepository.save(savedAd);
        }

        return adMapper.toAdDto(savedAd);
    }

    /**
     * Обновляет существующее объявление.
     * Проверяет, имеет ли пользователь право на редактирование.
     *
     * @param id идентификатор объявления
     * @param email email пользователя, пытающегося обновить объявление
     * @param createOrUpdateAd новые данные объявления
     * @return обновленное объявление
     * @throws AdNotFoundException если объявление не найдено
     * @throws AccessDeniedException если пользователь не автор и не админ
     */
    @Override
    @Transactional
    public Ad updateAd(Integer id, String email, CreateOrUpdateAd createOrUpdateAd) {
        log.info("Обновление объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на редактирование
        checkAdPermissions(adEntity, email);

        adMapper.updateAdEntityFromDto(createOrUpdateAd, adEntity);
        AdEntity updatedAd = adRepository.save(adEntity);

        return adMapper.toAdDto(updatedAd);
    }

    /**
     * Удаляет объявление.
     * Проверяет права доступа и удаляет связанное изображение.
     *
     * @param id идентификатор объявления
     * @param email email пользователя, пытающегося удалить объявление
     * @throws AdNotFoundException если объявление не найдено
     * @throws AccessDeniedException если пользователь не автор и не админ
     */
    @Override
    @Transactional
    public void deleteAd(Integer id, String email) {
        log.info("Удаление объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на удаление
        checkAdPermissions(adEntity, email);

        // Удаляем изображение, если оно существует
        if (adEntity.getImage() != null) {
            imageService.deleteImage(adEntity.getImage());
        }

        adRepository.delete(adEntity);
    }

    /**
     * Обновляет изображение объявления.
     * Старое изображение удаляется, новое сохраняется.
     *
     * @param id идентификатор объявления
     * @param email email пользователя, пытающегося обновить изображение
     * @param image новый файл изображения
     * @throws AdNotFoundException если объявление не найдено
     * @throws AccessDeniedException если пользователь не автор и не админ
     */
    @Override
    @Transactional
    public void updateAdImage(Integer id, String email, MultipartFile image) {
        log.info("Обновление картинки объявления с id: {} пользователем с email: {}", id, email);

        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        // Проверяем права на редактирование
        checkAdPermissions(adEntity, email);

        // Обновляем изображение (старое удалится автоматически)
        String newImagePath = imageService.updateImage(adEntity.getImage(), image, "ad", id);
        adEntity.setImage(newImagePath);
        adRepository.save(adEntity);
    }

    /**
     * Проверяет, имеет ли пользователь права на редактирование/удаление объявления.
     * Права есть если:
     * - пользователь является автором объявления
     * - пользователь является администратором
     *
     * @param adEntity проверяемое объявление
     * @param email email пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если у пользователя нет прав
     */
    private void checkAdPermissions(AdEntity adEntity, String email) {
        if (!adEntity.getAuthor().getEmail().equals(email)) {
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            if (!"ADMIN".equals(user.getRole().name())) {
                throw new AccessDeniedException("Нет прав на редактирование этого объявления");
            }
        }
    }
}
