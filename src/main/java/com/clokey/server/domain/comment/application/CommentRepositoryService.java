package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.model.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentRepositoryService {

    boolean commentExist(Long commentId);

    List<Comment> getRepliesByCommentId(Long commentId);

    Page<Comment> getNoneReplyCommentsByHistoryId(Long historyId, Integer page);
}
