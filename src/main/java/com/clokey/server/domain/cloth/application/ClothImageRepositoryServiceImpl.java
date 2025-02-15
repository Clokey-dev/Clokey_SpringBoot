package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.cloth.domain.repository.ClothImageRepository;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ClothImageRepositoryServiceImpl implements ClothImageRepositoryService{

    private final ClothImageRepository clothImageRepository;
    private final S3ImageService s3ImageService;

    @Override
    public void save(ClothImage clothImage) {
        clothImageRepository.save(clothImage);
    }

    @Override
    public void deleteByClothId(Long clothId) {
        clothImageRepository.deleteByClothId(clothId);
    }

    @Override
    public void deleteAllByClothIds(List<Long> ClothIds) {
        // 특정 historyIds에 해당하는 모든 이미지를 조회
        List<ClothImage> clothImages = clothImageRepository.findByCloth_IdIn(ClothIds);

        if(clothImages == null || clothImages.isEmpty()) {
            return;
        }

        // S3 및 데이터베이스에서 이미지 삭제
        clothImages.forEach(image -> {
            // S3에서 이미지 삭제
            s3ImageService.deleteImageFromS3(image.getImageUrl());

            // DB에서 엔티티 삭제
            clothImageRepository.delete(image);
        });
    }

}
