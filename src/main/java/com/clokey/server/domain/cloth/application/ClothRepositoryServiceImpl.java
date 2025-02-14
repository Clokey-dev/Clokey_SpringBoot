package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.domain.repository.ClothRepository;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.SummaryFrequency;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClothRepositoryServiceImpl implements ClothRepositoryService{

    private final ClothRepository clothRepository;

    @Override
    @EntityGraph(attributePaths = {"images"})
    public Cloth findById(Long id){
        return clothRepository.findById(id).orElseThrow(()->new DatabaseException(ErrorStatus.NO_SUCH_CLOTH));
    }

    @Override
    @Modifying
    public void deleteById(Long id){
        clothRepository.deleteById(id);
    }

    @Override
    public Cloth save(Cloth cloth) {
        return clothRepository.save(cloth);
    }

    @Override
    public boolean existsById(Long clothId) {
        return clothRepository.existsById(clothId);
    }

    @Override
    public Page<Cloth> findByClosetFilters(
            @Param("ownerClokeyId") String ownerClokeyId,
            @Param("requesterId") Long requesterId,
            @Param("categoryId") Long categoryId,
            @Param("season") Season season,
            @Param("sort") ClothSort sort,
            Pageable pageable
    ){
        return clothRepository.findByClosetFilters(ownerClokeyId, requesterId, categoryId, season, sort.toString(), pageable);
    }

    @Override
    public List<Cloth> findBySmartSummaryFilters(
            @Param("type") SummaryFrequency type,
            @Param("memberId") Long memberId,
            @Param("categoryId") Long categoryId
    ){
        return switch (type) {
            case FREQUENT -> clothRepository.findMostFrequentClothList(memberId,categoryId);
            case INFREQUENT -> clothRepository.findLeastFrequentClothList(memberId,categoryId);
        };
    }
  
    public List<Cloth> findAllById(List<Long> clothIds) {
        return clothRepository.findAllById(clothIds);

    }

    @Override
    public Page<Cloth> findByMemberInAndVisibilityOrderByCreatedAtDesc(List<Member> members, Visibility visibility, Pageable pageable) {
        return clothRepository.findByMemberInAndVisibilityOrderByCreatedAtDesc(members, visibility, pageable);
    }

    @Override
    public List<Cloth> findTop6ByMemberInAndVisibilityOrderByCreatedAtDesc(List<Member> members, Visibility visibility) {
        return clothRepository.findTop6ByMemberInAndVisibilityOrderByCreatedAtDesc(members, visibility);
    }

    @Override
    public String findMostWornCategory(Long memberId) {
        return clothRepository.findMostWornCategory(memberId)
                .orElse(null);
    }

    @Override
    public Cloth findSuitableCloth(Long memberId, Double nowTemp, String category) {
        return clothRepository.findSuitableCloth(memberId, nowTemp, category)
                .orElse(null);
    }
}
