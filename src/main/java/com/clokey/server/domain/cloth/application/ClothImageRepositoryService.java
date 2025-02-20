package com.clokey.server.domain.cloth.application;

import java.util.List;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;

public interface ClothImageRepositoryService {

    void save(ClothImage clothImage);

    void deleteByClothId(Long clothId);

    void deleteAllByClothIds(List<Long> ClothIds);

}
