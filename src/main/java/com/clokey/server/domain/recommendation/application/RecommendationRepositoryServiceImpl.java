package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import com.clokey.server.domain.recommendation.domain.repository.RecommendationRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRepositoryServiceImpl implements RecommendationRepositoryService {

    private final RecommendationRepository recommendationRepository;

    @Transactional
    @Override
    public void save(Recommendation recommendation) {
        recommendationRepository.save(recommendation);
    }

    @Transactional
    @Override
    public void deleteById(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Recommendation findById(Long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_RECOMMEND));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long recommendationId) {
        return recommendationRepository.existsById(recommendationId);
    }

    @Override
    public Page<Recommendation> findAllByMemberId(Long memberId, Pageable page) {
        return null;
    }

    @Override
    public List<Recommendation> findByMemberIdAndNewsTypeIn(Long memberId, List<NewsType> newsTypes) {
        return recommendationRepository.findByMemberIdAndNewsTypeIn(memberId, newsTypes);
    }

    @Override
    public void saveAll(List<Recommendation> recommendations) {
        recommendationRepository.saveAll(recommendations);
    }
}
