package com.clokey.server.domain.history.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HistoryResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyHistoryResult {
        Long memberId;
        Long historyId;
        String memberImageUrl;
        String nickName;
        String clokeyId;
        String contents;
        List<String> imageUrl;
        List<String> hashtags;
        boolean visibility;
        int likeCount;
        boolean isLiked;
        LocalDate date;
        List<HistoryClothResult> cloths;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryClothResult {
        Long clothId;
        String clothImageUrl;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthViewResult {
        Long memberId;
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
    @JsonPropertyOrder({"commentId", "memberId","nickName","userImageUrl", "content", "replyResults"})
    public static class CommentResult{
        Long commentId;
        Long memberId;
        String nickName;
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
        String nickName;
        String userImageUrl;
        String content;
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResult{
        Long historyId;
        boolean isLiked;
        int likeCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedUserResults{
        List<LikedUserResult> likedUsers;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikedUserResult{
        Long memberId;
        String clokeyId;
        String nickname;
        boolean followStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentWriteResult{
        Long commentId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreateResult{
        Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LastYearHistoryResult{
        Long historyId;
        String nickName;
        List<String> imageUrls;
    }

}





