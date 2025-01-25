package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;

public interface ClothImageRepositoryService {

    void save(ClothImage clothImage);

    void deleteAllByClothId(Long clothId);
}
