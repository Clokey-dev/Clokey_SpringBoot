package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.model.repository.CommentRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final CommentRepository commentRepository;

    public void validateParentCommentHistory(Long historyId,Long parentCommentId) {
        if(parentCommentId == null) {
            return;
        }

        Long parentHistoryId = commentRepository.findById(parentCommentId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_COMMENT)).getHistory().getId();

        if(!parentHistoryId.equals(historyId)) {
            throw new GeneralException(ErrorStatus.PARENT_COMMENT_HISTORY_ERROR);
        }
    }
}
