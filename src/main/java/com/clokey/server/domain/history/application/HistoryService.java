package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dto.HistoryRequestDto;
import com.clokey.server.domain.history.dto.HistoryResponseDto;

public interface HistoryService {

    HistoryResponseDto.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked);

    HistoryResponseDto.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content);

    HistoryResponseDto.DayViewResult getDaily(Long historyId, Long memberId);

    HistoryResponseDto.HistoryCommentResult getComments(Long historyId, int page);

    HistoryResponseDto.MonthViewResult getMonthlyHistories(Long this_member_id, Long memberId, String month);

}
