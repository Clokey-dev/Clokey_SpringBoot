package com.clokey.server.domain.comment.dao;

import com.clokey.server.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByHistoryIdAndCommentIsNull(Long historyId);

    List<Comment> findByCommentId(Long parentId);
}
