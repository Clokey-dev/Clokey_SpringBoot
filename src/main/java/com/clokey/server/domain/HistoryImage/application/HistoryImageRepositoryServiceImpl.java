package com.clokey.server.domain.HistoryImage.application;

import com.clokey.server.domain.HistoryImage.dao.HistoryImageRepository;
import com.clokey.server.domain.model.HistoryImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryImageRepositoryServiceImpl implements HistoryImageRepositoryService{

    private final HistoryImageRepository historyImageRepository;

    @Override
    public boolean historyImageExist(Long historyId) {
        return historyImageRepository.existsByHistory_Id(historyId);
    }

    @Override
    public List<String> getHistoryImageUrls(Long historyId) {
        List<HistoryImage> historyImages = historyImageRepository.findByHistory_Id(historyId);

        return historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();
    }
}
