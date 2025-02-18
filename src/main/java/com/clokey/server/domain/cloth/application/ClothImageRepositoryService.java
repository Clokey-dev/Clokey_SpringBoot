package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;

import java.util.List;

public interface ClothImageRepositoryService {

    void save(ClothImage clothImage);

    void deleteByClothId(Long clothId);

    void deleteAllByClothIds(List<Long> ClothIds);

}
