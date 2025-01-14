package com.clokey.server.domain.history.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HistoryResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayViewResult {
        Long userId;
        String contents;
        List<String> imageUrl;
        List<String> hashtags;
        boolean visibility;
        int likeCount;
        boolean isLiked;
        LocalDate date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthViewResult {
        Long userId;
        List<HistoryResult> histories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryResult {
        Long historyId;
        LocalDate date;
        String imageUrl;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCommentResult {
        List<CommentResult> comments;
        int totalPage;
        int totalElements;
        boolean isFirst;
        boolean isLast;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonPropertyOrder({"commentId", "memberId", "userImageUrl", "content", "replyResults"})
    public static class CommentResult{
        Long commentId;
        Long memberId;
        String userImageUrl;
        String content;
        List<ReplyResult> replyResults;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyResult{
        Long commentId;
        Long MemberId;
        String userImageUrl;
        String content;
    }

}


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class likeResult{
        Long historyId;
        boolean isLiked;
        int likeCount;
    }
}


