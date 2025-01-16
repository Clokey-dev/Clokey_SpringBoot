package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.comment.application.CommentRepositoryService;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepositoryService commentRepositoryService;

    public void validateParentCommentHistory(Long historyId,Long parentCommentId) {
        if(parentCommentId == null) {
            return;
        }

        Long parentHistoryId = commentRepositoryService.findById(parentCommentId).get().getHistory().getId();

        if(!parentHistoryId.equals(historyId)) {
            throw new GeneralException(ErrorStatus.PARENT_COMMENT_HISTORY_ERROR);
        }
    }
}
