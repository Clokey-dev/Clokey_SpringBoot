package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.comment.dao.CommentRepository;
import com.clokey.server.domain.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public List<List<Comment>> getReplyListOfCommentList(Page<Comment> comments) {
        return comments.stream()
                .map(comment -> commentRepository.findByCommentId(comment.getId()))
                .toList();
    }

    @Override
    public Page<Comment> getNoneReplyCommentsByHistoryId(Long historyId, Integer page) {
        return commentRepository.findByHistoryIdAndCommentIsNull(historyId, PageRequest.of(page,10,Sort.by(Sort.Direction.ASC, "createdAt")));
    }
}
