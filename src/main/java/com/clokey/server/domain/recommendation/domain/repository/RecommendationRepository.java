package com.clokey.server.domain.recommendation.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes);
}
