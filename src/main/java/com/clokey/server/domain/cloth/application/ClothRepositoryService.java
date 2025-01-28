package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;

public interface ClothRepositoryService {

    Cloth findById(Long id);

    void deleteById(Long id);

    Cloth save(Cloth cloth);

    boolean existsById(Long clothId);
}
