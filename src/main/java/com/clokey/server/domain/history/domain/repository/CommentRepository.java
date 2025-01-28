package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.Comment;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByHistoryIdAndCommentIsNull(Long historyId, PageRequest pageRequest);

    List<Comment> findByCommentId(Long parentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.comment.id = :commentId")
    void deleteChildren(@Param("commentId") Long commentId);

}
