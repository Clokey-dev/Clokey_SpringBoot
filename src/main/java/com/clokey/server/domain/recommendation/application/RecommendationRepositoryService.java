package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecommendationRepositoryService {

    void save(Recommendation recommendation);

    void deleteById(Long recommendationId);

    Recommendation findById(Long recommendationId);

    boolean existsById(Long recommendationId);

    Page<Recommendation> findAllByMemberId(Long memberId, Pageable page);

    List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes);

    void saveAll(List<Recommendation> recommendations);
}
