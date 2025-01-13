package com.clokey.server.domain.history.converter;

import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;

import java.util.ArrayList;
import java.util.List;

public class HistoryConverter {

    public static HistoryResponseDto.DayViewResult toDayViewResult(History history, List<String> imageUrl, List<String> hashtags, int likeCount, boolean isLiked) {
        return HistoryResponseDto.DayViewResult.builder()
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

    public static HistoryResponseDto.MonthViewResult toPublicMonthViewResult(Long memberId, List<History> histories , List<String> historyFirstImageUrls) {

        List<HistoryResponseDto.HistoryResult> HistoryResults = new ArrayList<>();

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);

            String historyImageUrl;

            //공개된 경우 사진 URL을 가져오고 아닌 경우 "비공개입니다"를 반환합니다.
            if (history.getVisibility().equals(Visibility.PUBLIC)){
                historyImageUrl = historyFirstImageUrls.get(i);
            } else {
                historyImageUrl = "비공개입니다";
            }

            HistoryResults.add(toHistoryResult(history, historyImageUrl));
        }

        return HistoryResponseDto.MonthViewResult.builder()
                .userId(memberId)
                .histories(HistoryResults)
                .build();
    }

    public static HistoryResponseDto.MonthViewResult toAllMonthViewResult(Long memberId, List<History> histories , List<String> historyFirstImageUrls) {

        List<HistoryResponseDto.HistoryResult> HistoryResults = new ArrayList<>();

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            String historyImageUrl = historyFirstImageUrls.get(i);

            HistoryResults.add(toHistoryResult(history, historyImageUrl));
        }

        return HistoryResponseDto.MonthViewResult.builder()
                .userId(memberId)
                .histories(HistoryResults)
                .build();
    }

    private static HistoryResponseDto.HistoryResult toHistoryResult(History history, String historyImageUrl) {
        return HistoryResponseDto.HistoryResult.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(historyImageUrl)
                .build();
    }
}

