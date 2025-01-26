package com.clokey.server.domain.history.application;


import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.domain.repository.HistoryImageRepository;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HistoryImageRepositoryServiceImpl implements HistoryImageRepositoryService{

    private final HistoryImageRepository historyImageRepository;
    private final S3ImageService s3ImageService;

    @Override
    public boolean existsByHistory_Id(Long historyId) {
        return historyImageRepository.existsByHistory_Id(historyId);
    }

    @Override
    public List<HistoryImage> findByHistory_Id(Long historyId) {
        return findByHistory_Id(historyId);
    }

    @Override
    public HistoryImage save(HistoryImage historyImage) {
        return historyImageRepository.save(historyImage);
    }

    public void deleteAllByHistory_Id(Long historyId) {
        // 특정 historyId에 해당하는 모든 이미지를 조회
        List<HistoryImage> historyImages = historyImageRepository.findByHistory_Id(historyId);

        // S3 및 데이터베이스에서 이미지 삭제
        historyImages.forEach(image -> {
            // S3에서 이미지 삭제
            s3ImageService.deleteImageFromS3(image.getImageUrl());

            // DB에서 엔티티 삭제
            historyImageRepository.delete(image);
        });
    }
}
