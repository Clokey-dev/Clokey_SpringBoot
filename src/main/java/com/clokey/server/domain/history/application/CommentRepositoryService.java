package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CommentRepositoryService {

    Page<Comment> findByHistoryIdAndCommentIsNull(Long historyId, PageRequest pageRequest);

    List<Comment> findByCommentId(Long parentId);

    Comment findById(Long commentId);

    Comment save(Comment comment);

    boolean existsById(Long commentId);
}
