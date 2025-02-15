package com.clokey.server.domain.history.application;


import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.domain.repository.HistoryImageRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HistoryImageRepositoryServiceImpl implements HistoryImageRepositoryService{

    private final HistoryImageRepository historyImageRepository;
    private final S3ImageService s3ImageService;

    @Override
    public List<HistoryImage> findByHistoryId(Long historyId) {
        return historyImageRepository.findByHistory_Id(historyId);
    }

    @Override
    public void save(MultipartFile image, History history) {
        if(image == null || image.isEmpty()){
            throw new DatabaseException(ErrorStatus.S3_EMPTY_FILE_EXCEPTION);
        }
        String url = s3ImageService.upload(image);

        historyImageRepository.save(HistoryImage.builder()
                        .imageUrl(url)
                        .history(history)
                        .build());
    }

    @Override
    public void save(List<MultipartFile> images, History history) {
        if (images == null || images.isEmpty()){
            throw new DatabaseException(ErrorStatus.S3_EMPTY_FILE_EXCEPTION);
        }

        images.forEach(image->save(image,history));

    }

    @Override
    public void deleteAllByHistoryId(Long historyId) {
        // 특정 historyId에 해당하는 모든 이미지를 조회
        List<HistoryImage> historyImages = historyImageRepository.findByHistory_Id(historyId);

        if(historyImages == null || historyImages.isEmpty()) {
            return;
        }

        // S3 및 데이터베이스에서 이미지 삭제
        historyImages.forEach(image -> {
            // S3에서 이미지 삭제
            s3ImageService.deleteImageFromS3(image.getImageUrl());

            // DB에서 엔티티 삭제
            historyImageRepository.delete(image);
        });
    }

    @Override
    public void deleteAllByHistoryIds(List<Long> historyIds) {
        // 특정 historyIds에 해당하는 모든 이미지를 조회
        List<HistoryImage> historyImages = historyImageRepository.findByHistory_IdIn(historyIds);

        if(historyImages == null || historyImages.isEmpty()) {
            return;
        }

        // S3 및 데이터베이스에서 이미지 삭제
        historyImages.forEach(image -> {
            // S3에서 이미지 삭제
            s3ImageService.deleteImageFromS3(image.getImageUrl());

            // DB에서 엔티티 삭제
            historyImageRepository.delete(image);
        });
    }
}
