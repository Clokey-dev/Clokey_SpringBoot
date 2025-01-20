package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.model.Cloth;

import java.util.Optional;

public interface ClothService {

    boolean clothExist(Long clothId);

    boolean isPublic(Long clothId);

    Optional<Cloth> getClothById(Long clothId);
}
