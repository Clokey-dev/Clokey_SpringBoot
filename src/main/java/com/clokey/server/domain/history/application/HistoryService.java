package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dto.HistoryRequestDto;
import com.clokey.server.domain.history.dto.HistoryResponseDto;

public interface HistoryService {

    void changeLike(Long memberId, Long historyId, boolean isLiked);

    HistoryResponseDto.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content);
}
