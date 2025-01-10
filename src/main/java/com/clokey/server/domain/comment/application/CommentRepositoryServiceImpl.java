package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.comment.dao.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentRepositoryServiceImpl implements CommentRepositoryService{

    private final CommentRepository commentRepository;

    @Override
    public boolean commentExist(Long commentId) {
        return commentRepository.existsById(commentId);
    }
}
