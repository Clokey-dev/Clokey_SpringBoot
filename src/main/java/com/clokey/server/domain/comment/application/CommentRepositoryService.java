package com.clokey.server.domain.comment.application;

import com.clokey.server.domain.model.Comment;
import org.springframework.data.domain.Page;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CommentRepositoryService {

    boolean commentExist(Long commentId);

    List<List<Comment>> getReplyListOfCommentList(Page<Comment> comments);

    Page<Comment> getNoneReplyCommentsByHistoryId(Long historyId, Integer page);

    Optional<Comment> findById(Long commentId);
}
