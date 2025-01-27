package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HistoryService {

    HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked);

    HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content);

    HistoryResponseDTO.DayViewResult getDaily(Long historyId, Long memberId);

    HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page);

    HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long this_member_id, Long memberId, String month);

    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreate historyCreateRequest, Long memberId, List<MultipartFile> images);

    void updateHistory(HistoryRequestDTO.HistoryUpdate historyUpdate, Long memberId, Long historyId, List<MultipartFile> images);

    void deleteComment(Long commentId,Long memberId);

    void updateComment(HistoryRequestDTO.UpdateComment updateCommentRequest,Long commentId,Long memberId);
}
