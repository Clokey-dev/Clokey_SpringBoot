package com.clokey.server.domain.recommendation.domain.repository;

import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findTop6ByNewsTypeOrderByCreatedAtDesc(NewsType newsType);
    List<Recommendation> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Recommendation> findTop1ByMemberIdAndNewsTypeOrderByCreatedAtDesc(Long memberId, NewsType newsType);
    List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes);
}
