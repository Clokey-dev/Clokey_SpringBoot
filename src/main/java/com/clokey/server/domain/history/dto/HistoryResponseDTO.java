package com.clokey.server.domain.history.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        Long commentCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryClothResult {
        Long clothId;
        String clothImageUrl;
        String clothName;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthViewResult {
        Long memberId;
        String nickName;
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

        @JsonProperty("isFirst") // JSON 직렬화 시 "isFirst" 사용
        private boolean isFirst;

        @JsonIgnore // "first" 필드 직렬화 방지
        public boolean isFirst() {
            return isFirst;
        }

        @JsonProperty("isLast") // JSON 직렬화 시 "isLast" 사용
        private boolean isLast;

        @JsonIgnore // "last" 필드 직렬화 방지
        public boolean isLast() {
            return isLast;
        }
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
        String imageUrl;
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
    public static class HistoryCreateResult {
        Long historyId;
    }
}





