package com.vshevchenko.resaleplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vshevchenko.resaleplatform.entity.CommentEntity;

import java.util.List;

/**
 * Репозиторий для работы с комментариями.
 * Предоставляет методы для доступа к данным таблицы comments.
 */
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByAdPk(Integer adPk);

    void deleteAllByAdPk(Integer adPk);
}
