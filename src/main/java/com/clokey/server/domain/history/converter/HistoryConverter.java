package com.clokey.server.domain.history.converter;

import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;

import java.util.ArrayList;
import java.util.List;

public class HistoryConverter {

    public static HistoryResponseDto.dayViewResult toDayViewResult(History history, List<String> imageUrl, List<String> hashtags, int likeCount, boolean isLiked) {
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

    public static HistoryResponseDto.monthViewResult toMonthViewResult(Long memberId, List<History> histories , List<String> historyFirstImageUrls) {

        List<HistoryResponseDto.historyResult> historyResults = new ArrayList<>();

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            String historyImageUrl = historyFirstImageUrls.get(i);

            historyResults.add(toHistoryResult(history, historyImageUrl));
        }

        return HistoryResponseDto.monthViewResult.builder()
                .userId(memberId)
                .histories(historyResults)
                .build();
    }

    private static HistoryResponseDto.historyResult toHistoryResult(History history, String historyImageUrl) {
        return HistoryResponseDto.historyResult.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(historyImageUrl)
                .build();
    }
}

