package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;

public interface RecommendationService {
    RecommendationResponseDTO.DailyClothesResult getRecommendClothes(Long memberId, Float nowTemp);
    RecommendationResponseDTO.DailyNewsResult getIssues(Long memberId, String view, String section, Integer page);
}
