package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.cloth.domain.repository.ClothImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class ClothImageRepositoryServiceImpl implements ClothImageRepositoryService{

    private final ClothImageRepository clothImageRepository;


    @Override
    public void save(ClothImage clothImage) {
        clothImageRepository.save(clothImage);
    }

    @Override
    public void deleteAllByClothId(Long clothId) {
        clothImageRepository.deleteAllByClothId(clothId);
    }
}
