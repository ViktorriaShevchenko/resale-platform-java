package com.vshevchenko.resaleplatform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.vshevchenko.resaleplatform.dto.Ad;
import com.vshevchenko.resaleplatform.dto.Ads;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateAd;
import com.vshevchenko.resaleplatform.dto.ExtendedAd;
import com.vshevchenko.resaleplatform.service.AdService;
import com.vshevchenko.resaleplatform.exception.AdNotFoundException;

/**
 * Контроллер для управления объявлениями.
 * <p>
 * Предоставляет REST API для выполнения операций с объявлениями:
 * просмотр всех объявлений, создание нового, получение по ID,
 * обновление, удаление, получение объявлений текущего пользователя,
 * обновление изображения объявления.
 * </p>
 *
 * @author ViktorriaShevchenko
 * @version 1.0
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/ads")
@RequiredArgsConstructor
@Tag(name = "Объявления")
public class AdsController {

    private final AdService adService;

    /**
     * Получает список всех объявлений.
     * <p>
     * Доступен без аутентификации.
     * </p>
     *
     * @return список всех объявлений с общей информацией
     */
    @Operation(
            summary = "Получение всех объявлений",
            responses = @ApiResponse(responseCode = "200", description = "OK")
    )
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());
    }

    /**
     * Создает новое объявление.
     * <p>
     * Доступно только авторизованным пользователям с ролями USER или ADMIN.
     * </p>
     *
     * @param properties данные объявления (название, цена, описание)
     * @param image файл изображения для объявления
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return созданное объявление с присвоенным ID
     */
    @Operation(
            summary = "Добавление объявления",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @RequestPart("properties") CreateOrUpdateAd properties,
            @RequestPart("image") MultipartFile image,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Ad createdAd = adService.createAd(authentication.getName(), properties, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAd);
    }

    /**
     * Получает подробную информацию об объявлении по его ID.
     * <p>
     * Доступен без аутентификации.
     * </p>
     *
     * @param id идентификатор объявления
     * @return расширенная информация об объявлении
     * @throws AdNotFoundException если объявление не найдено
     */
    @Operation(
            summary = "Получение информации об объявлении",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAds(@PathVariable Integer id) {
        return ResponseEntity.ok(adService.getAdById(id));
    }

    /**
     * Удаляет объявление по его ID.
     * <p>
     * Доступно только автору объявления или администратору.
     * </p>
     *
     * @param id идентификатор объявления
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return пустой ответ с кодом 204
     * @throws AdNotFoundException если объявление не найдено
     * @throws org.springframework.security.access.AccessDeniedException если нет прав на удаление
     */
    @Operation(
            summary = "Удаление объявления",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable Integer id,
                                         Authentication authentication) {
        adService.deleteAd(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет информацию об объявлении.
     * <p>
     * Доступно только автору объявления или администратору.
     * </p>
     *
     * @param id идентификатор объявления
     * @param ad новые данные объявления
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return обновленное объявление
     */
    @Operation(
            summary = "Обновление информации об объявлении",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAds(@PathVariable Integer id,
                                        @RequestBody CreateOrUpdateAd ad,
                                        Authentication authentication) {
        return ResponseEntity.ok(adService.updateAd(id, authentication.getName(), ad));
    }

    /**
     * Получает все объявления текущего авторизованного пользователя.
     *
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return список объявлений пользователя
     */
    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {

        if (authentication == null) {
            log.warn("Попытка доступа к /ads/me без аутентификации");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(adService.getAdsByUser(authentication.getName()));
    }

    /**
     * Обновляет изображение объявления.
     * <p>
     * Доступно только автору объявления или администратору.
     * </p>
     *
     * @param id идентификатор объявления
     * @param image новый файл изображения
     * @param authentication объект аутентификации для получения email текущего пользователя
     * @return пустой ответ с кодом 200
     */
    @Operation(
            summary = "Обновление картинки объявления",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestParam("image") MultipartFile image,
                                              Authentication authentication) {
        adService.updateAdImage(id, authentication.getName(), image);
        return ResponseEntity.ok(new byte[0]);
    }
}
