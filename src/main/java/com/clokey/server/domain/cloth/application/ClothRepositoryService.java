package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothRepositoryService {

    Cloth findById(Long id);

    void deleteById(Long id);

    Cloth save(Cloth cloth);

    boolean existsById(Long clothId);

    Page<Cloth> findByFilters(
            @Param("clokeyId") String clokeyId,
            @Param("requesterId") Long requesterId,
            @Param("categoryId") Long categoryId,
            @Param("season") Season season,
            @Param("sort") ClothSort sort,
            Pageable pageable
    );

    List<Cloth> findAllById(List<Long> clothIds);
}
