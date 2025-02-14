package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.domain.repository.ClothRepository;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.SummaryFrequency;
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

    public List<Cloth> findAll(){
        return clothRepository.findAll();
    }
}
