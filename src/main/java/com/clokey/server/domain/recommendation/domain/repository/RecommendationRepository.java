package com.clokey.server.domain.recommendation.domain.repository;

import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes);
}
