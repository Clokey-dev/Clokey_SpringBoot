package com.clokey.server.domain.history.dto;

import com.clokey.server.domain.model.BaseEntity;
import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.model.enums.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class dayViewResult{
        Long memberId;
        String contents;
        List<String> imageUrl;
        List<String> hashtags;
        String visibility;
        Integer likeCount;

    }
}





