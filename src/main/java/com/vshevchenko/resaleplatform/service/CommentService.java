package com.vshevchenko.resaleplatform.service;

import com.vshevchenko.resaleplatform.dto.Comment;
import com.vshevchenko.resaleplatform.dto.Comments;
import com.vshevchenko.resaleplatform.dto.CreateOrUpdateComment;

public interface CommentService {

    Comments getCommentsByAdId(Integer adId);
    Comment addComment(Integer adId, String email, CreateOrUpdateComment createOrUpdateComment);
    void deleteComment(Integer adId, Integer commentId, String email);
    Comment updateComment(Integer adId, Integer commentId, String email,
                          CreateOrUpdateComment createOrUpdateComment);
}
