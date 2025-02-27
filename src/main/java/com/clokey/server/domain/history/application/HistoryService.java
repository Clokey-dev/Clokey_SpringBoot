package com.clokey.server.domain.history.application;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;

public interface HistoryService {

    HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked);

    HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content);

    HistoryResponseDTO.DailyHistoryResult getDaily(Long historyId, Long memberId);

    HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page);

    HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long myMemberId, String clokeyId, String month);

    HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreate historyCreateRequest, Long memberId, List<MultipartFile> images);

    void deleteComment(Long commentId, Long memberId);

    void updateComment(HistoryRequestDTO.UpdateComment updateCommentRequest, Long commentId, Long memberId);

    void deleteHistory(Long historyId, Long memberId);

    HistoryResponseDTO.CheckMyHistoryResult checkIfHistoryIsMine(Long historyId, Long memberId);

    HistoryResponseDTO.LikedUserResults getLikedUser(Long memberId, Long historyId);
}
