package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByHistoryIdAndCommentIsNull(Long historyId, PageRequest pageRequest);

    List<Comment> findByCommentId(Long parentId);

}
