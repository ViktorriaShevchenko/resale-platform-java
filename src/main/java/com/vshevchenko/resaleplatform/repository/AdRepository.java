package com.vshevchenko.resaleplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vshevchenko.resaleplatform.entity.AdEntity;

import java.util.List;

/**
 * Репозиторий для работы с объявлениями.
 * Предоставляет методы для доступа к данным таблицы ads.
 */
@Repository
public interface AdRepository extends JpaRepository<AdEntity, Integer> {
    List<AdEntity> findByAuthorId(Integer authorId);
}
