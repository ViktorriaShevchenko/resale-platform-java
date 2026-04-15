package com.vshevchenko.resaleplatform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vshevchenko.resaleplatform.dto.Comment;
import com.vshevchenko.resaleplatform.dto.Comments;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateComment;
import com.vshevchenko.resaleplatform.entity.AdEntity;
import com.vshevchenko.resaleplatform.entity.CommentEntity;
import com.vshevchenko.resaleplatform.entity.UserEntity;
import com.vshevchenko.resaleplatform.exception.AdNotFoundException;
import com.vshevchenko.resaleplatform.exception.CommentNotFoundException;
import com.vshevchenko.resaleplatform.exception.UserNotFoundException;
import com.vshevchenko.resaleplatform.mapper.CommentMapper;
import com.vshevchenko.resaleplatform.repository.AdRepository;
import com.vshevchenko.resaleplatform.repository.CommentRepository;
import com.vshevchenko.resaleplatform.repository.UserRepository;

import java.util.List;

/**
 * Реализация сервиса для работы с комментариями.
 * Содержит бизнес-логику создания, обновления, удаления и получения комментариев.
 * Выполняет проверку прав доступа к операциям с комментариями.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * Получает все комментарии к указанному объявлению.
     *
     * @param adId идентификатор объявления
     * @return объект Comments с количеством комментариев и их списком
     */
    @Override
    public Comments getCommentsByAdId(Integer adId) {
        log.info("Получение комментариев для объявления с id: {}", adId);

        List<CommentEntity> entities = commentRepository.findByAdPk(adId);
        List<Comment> commentList = commentMapper.toCommentDtoList(entities);

        Comments comments = new Comments();
        comments.setCount(commentList.size());
        comments.setResults(commentList);

        return comments;
    }

    /**
     * Добавляет новый комментарий к объявлению.
     *
     * @param adId идентификатор объявления
     * @param email email автора комментария
     * @param createOrUpdateComment текст комментария
     * @return созданный комментарий
     * @throws AdNotFoundException если объявление не найдено
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    @Transactional
    public Comment addComment(Integer adId, String email, CreateOrUpdateComment createOrUpdateComment) {
        log.info("Добавление комментария к объявлению с id: {} пользователем с email: {}", adId, email);

        AdEntity ad = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Объявление не найдено"));

        UserEntity author = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        CommentEntity commentEntity = commentMapper.toCommentEntity(createOrUpdateComment);
        commentEntity.setAd(ad);
        commentEntity.setAuthor(author);
        commentEntity.setCreatedAt(System.currentTimeMillis());

        CommentEntity savedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentDto(savedComment);
    }

    /**
     * Удаляет комментарий.
     * Проверяет, что комментарий относится к указанному объявлению,
     * и что пользователь имеет права на удаление.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param email email пользователя, пытающегося удалить комментарий
     * @throws CommentNotFoundException если комментарий не найден
     * @throws IllegalArgumentException если комментарий не относится к указанному объявлению
     * @throws AccessDeniedException если у пользователя нет прав на удаление
     */
    @Override
    @Transactional
    public void deleteComment(Integer adId, Integer commentId, String email) {
        log.info("Удаление комментария с id: {} из объявления с id: {} пользователем с email: {}",
                commentId, adId, email);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий действительно относится к указанному объявлению
        if (!commentEntity.getAd().getPk().equals(adId)) {
            log.error("Комментарий с id {} не относится к объявлению с id {}", commentId, adId);
            throw new IllegalArgumentException("Комментарий не относится к указанному объявлению");
        }

        // Проверяем права на удаление
        checkCommentPermissions(commentEntity, email);

        commentRepository.delete(commentEntity);
    }

    /**
     * Обновляет существующий комментарий.
     * Проверяет, что комментарий относится к указанному объявлению,
     * и что пользователь имеет права на редактирование.
     *
     * @param adId идентификатор объявления
     * @param commentId идентификатор комментария
     * @param email email пользователя, пытающегося обновить комментарий
     * @param createOrUpdateComment новый текст комментария
     * @return обновленный комментарий
     * @throws CommentNotFoundException если комментарий не найден
     * @throws IllegalArgumentException если комментарий не относится к указанному объявлению
     * @throws AccessDeniedException если у пользователя нет прав на редактирование
     */
    @Override
    @Transactional
    public Comment updateComment(Integer adId, Integer commentId, String email,
                                 CreateOrUpdateComment createOrUpdateComment) {
        log.info("Обновление комментария с id: {} из объявления с id: {} пользователем с email: {}",
                commentId, adId, email);

        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));

        // Проверяем, что комментарий действительно относится к указанному объявлению
        if (!commentEntity.getAd().getPk().equals(adId)) {
            log.error("Комментарий с id {} не относится к объявлению с id {}", commentId, adId);
            throw new IllegalArgumentException("Комментарий не относится к указанному объявлению");
        }

        // Проверяем права на редактирование
        checkCommentPermissions(commentEntity, email);

        commentMapper.updateCommentEntityFromDto(createOrUpdateComment, commentEntity);
        CommentEntity updatedComment = commentRepository.save(commentEntity);

        return commentMapper.toCommentDto(updatedComment);
    }

    /**
     * Проверяет, имеет ли пользователь права на редактирование/удаление комментария.
     * Права есть если:
     * - пользователь является автором комментария
     * - пользователь является администратором
     *
     * @param commentEntity проверяемый комментарий
     * @param email email пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws AccessDeniedException если у пользователя нет прав
     */
    private void checkCommentPermissions(CommentEntity commentEntity, String email) {
        // Если пользователь не автор и не админ, то выбрасываем исключение
        if (!commentEntity.getAuthor().getEmail().equals(email)) {
            // Проверяем, может быть это админ?
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

            if (!"ADMIN".equals(user.getRole().name())) {
                throw new AccessDeniedException("Нет прав на редактирование этого комментария");
            }
        }
    }
}
