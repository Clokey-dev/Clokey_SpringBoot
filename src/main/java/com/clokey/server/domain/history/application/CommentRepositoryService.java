package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepositoryService {

    Page<Comment> findByHistoryIdAndCommentIsNull(Long historyId, PageRequest pageRequest);

    List<Comment> findByCommentId(Long parentId);

    Comment findById(Long commentId);

    Comment save(Comment comment);

    boolean existsById(Long commentId);

    void deleteChildren(Long commentId);

    void deleteById(Long commentId);

    void deleteAllComments(Long HistoryId);


    boolean existsByIdAndMemberId(Long id, Long memberId);

    boolean existsByIdAndHistoryId(Long id, Long historyId);

    Long countByHistoryId(Long historyId);

    void deleteCommentsByHistoryIds(List<Long> historyId);

    void deleteChildrenByHistoryIds(List<Long> commentIds);

    void deleteChildrenByCommentIds(List<Long> commentIds);

    void deleteCommentsByCommentIds(List<Long> commentIds);

}
