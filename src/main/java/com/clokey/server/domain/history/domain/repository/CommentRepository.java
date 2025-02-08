package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.Comment;
import com.google.firebase.internal.NonNull;
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

    //햐나의 댓글을 기준으로 대댓글을 삭제합니다.
    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.comment.id = :commentId")
    void deleteChildren(@Param("commentId") Long commentId);

    //하나의 기록을 기준으로 대댓글을 모두 삭제합니다.
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.history.id = :historyId AND c.comment IS NOT NULL")
    void deleteRepliesByHistoryId(@Param("historyId") Long historyId);

    //하나의 기록을 기준으로 댓글을 모두 삭제합니다.
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.history.id = :historyId")
    void deleteParentCommentsByHistoryId(@Param("historyId") Long historyId);

    boolean existsByIdAndMemberId(Long id, Long memberId);

    boolean existsByIdAndHistoryId(Long id, Long historyId);

}
