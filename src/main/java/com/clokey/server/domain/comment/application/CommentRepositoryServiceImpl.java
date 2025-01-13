package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.comment.dao.CommentRepository;
import com.clokey.server.domain.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentRepositoryServiceImpl implements CommentRepositoryService{

    private final CommentRepository commentRepository;

    @Override
    public boolean commentExist(Long commentId) {
        return commentRepository.existsById(commentId);
    }

    @Override
    public List<Comment> getRepliesByCommentId(Long commentId) {
        return commentRepository.findByCommentId(commentId);
    }

    @Override
    public List<Comment> getNoneReplyCommentsByHistoryId(Long historyId) {
        return commentRepository.findByHistoryIdAndCommentIsNull(historyId);
    }
}
