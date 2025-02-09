package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepositoryService {

    void save(Recommendation recommendation);

    void deleteById(Long recommendationId);

    Recommendation findById(Long recommendationId);

    boolean existsById(Long recommendationId);

    Page<Recommendation> findAllByMemberId(Long memberId, Pageable page);

    List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes);

    void saveAll(List<Recommendation> recommendations);

    List<Recommendation> findTop6ByNewsTypeOrderByCreatedAtDesc(NewsType newsType);

    List<Recommendation> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    Optional<Recommendation> findTop1ByMemberIdAndNewsTypeOrderByCreatedAtDesc(Long memberId, NewsType newsType);
}
