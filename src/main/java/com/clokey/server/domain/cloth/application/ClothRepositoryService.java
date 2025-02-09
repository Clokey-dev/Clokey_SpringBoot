package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.SummaryFrequency;
import com.clokey.server.domain.model.entity.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothRepositoryService {

    Cloth findById(Long id);

    void deleteById(Long id);

    Cloth save(Cloth cloth);

    boolean existsById(Long clothId);

    Page<Cloth> findByClosetFilters(
            @Param("clokeyId") String clokeyId,
            @Param("requesterId") Long requesterId,
            @Param("categoryId") Long categoryId,
            @Param("season") Season season,
            @Param("sort") ClothSort sort,
            Pageable pageable
    );

    List<Cloth> findBySmartSummaryFilters(
            @Param("type") SummaryFrequency type,
            @Param("memberId") Long memberId,
            @Param("categoryId") Long categoryId
    );

    List<Cloth> findAllById(List<Long> clothIds);

    Page<Cloth> findByMemberInAndVisibilityOrderByCreatedAtDesc(
            List<Member> members, Visibility visibility, Pageable pageable);

    List<Cloth> findTop6ByMemberInAndVisibilityOrderByCreatedAtDesc(
            List<Member> members, Visibility visibility);

}
