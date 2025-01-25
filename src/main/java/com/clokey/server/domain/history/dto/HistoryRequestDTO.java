package com.clokey.server.domain.history.dto;

import com.clokey.server.domain.history.exception.annotation.ContentLength;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.history.exception.annotation.ParentCommentConditions;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class HistoryRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCreate {

        String content;

        List<Long> clothes;

        List<String> hashtags;

        Visibility visibility;

        String date;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeStatusChange {

        @HistoryExist
        Long historyId;

        @JsonProperty("liked")
        boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentWrite {


        @ParentCommentConditions
        Long commentId;

        @ContentLength
        @NotBlank
        String content;
    }


}
