package com.clokey.server.domain.history.application;


import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.domain.repository.HistoryImageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HistoryImageRepositoryServiceImpl implements HistoryImageRepositoryService{

    private final HistoryImageRepository historyImageRepository;

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
}
