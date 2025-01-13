package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.model.Comment;

import java.util.List;

public interface CommentRepositoryService {

    boolean commentExist(Long commentId);

    List<Comment> getRepliesByCommentId(Long commentId);

    List<Comment> getNoneReplyCommentsByHistoryId(Long historyId);
}
