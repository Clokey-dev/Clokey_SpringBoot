package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryConverter {

    public static HistoryResponseDto.dayViewResult toDayViewResult(History history,List<String> imageUrl, List<String> hashtags, int likeCount, boolean isLiked){
        return HistoryResponseDto.dayViewResult.builder()
                .userId(history.getMember().getId())
                .contents(history.getContent())
                .imageUrl(imageUrl)
                .hashtags(hashtags)
                .visibility(history.getVisibility().equals(Visibility.PUBLIC))
                .likeCount(likeCount)
                .isLiked(isLiked)
                .date(history.getHistoryDate())
                .build();
    }
}
