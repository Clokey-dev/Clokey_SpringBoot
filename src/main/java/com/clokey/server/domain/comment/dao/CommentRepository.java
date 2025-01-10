package com.clokey.server.domain.comment.dao;

import com.clokey.server.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
