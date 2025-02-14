package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;

public interface RecommendationService {
    RecommendationResponseDTO.DailyClothesResult getRecommendClothes(Long memberId, Double nowTemp, Double minTemp, Double maxTemp);
    RecommendationResponseDTO.DailyNewsResult getNews(Long memberId);
    RecommendationResponseDTO.DailyNewsAllResult<?> getNewsAll(Long memberId, String section, Integer page);
    HistoryResponseDTO.LastYearHistoryResult getLastYearHistory(Long memberId);
}
