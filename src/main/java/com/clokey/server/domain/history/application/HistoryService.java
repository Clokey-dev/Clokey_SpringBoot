package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked);

    HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content);

    HistoryResponseDTO.DayViewResult getDaily(Long historyId, Long memberId);

    HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page);

    HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long this_member_id, Long memberId, String month);

}
