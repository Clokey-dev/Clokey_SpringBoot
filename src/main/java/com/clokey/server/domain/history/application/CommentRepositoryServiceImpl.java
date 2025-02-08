package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.repository.CommentRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentRepositoryServiceImpl implements CommentRepositoryService{

    private final CommentRepository commentRepository;

    @Override
    public Page<Comment> findByHistoryIdAndCommentIsNull(Long historyId, PageRequest pageRequest) {
        return commentRepository.findByHistoryIdAndCommentIsNull(historyId,pageRequest);
    }

    @Override
    public List<Comment> findByCommentId(Long parentId) {
        return commentRepository.findByCommentId(parentId);
    }

    @Override
    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_COMMENT));
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public boolean existsById(Long commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    public void deleteChildren(Long commentId) {
        commentRepository.deleteChildren(commentId);
    }

    @Override
    public void deleteById(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAllComments(Long historyId) {
        commentRepository.deleteRepliesByHistoryId(historyId);
        commentRepository.deleteParentCommentsByHistoryId(historyId);
    }

    @Override
    public boolean existsByIdAndMemberId(Long id, Long memberId) {
        return commentRepository.existsByIdAndMemberId(id,memberId);
    }

    @Override
    public boolean existsByIdAndHistoryId(Long id, Long historyId) {
        return commentRepository.existsByIdAndHistoryId(id, historyId);
    }

}
