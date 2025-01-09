package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dao.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryRepositoryServiceImpl implements HistoryRepositoryService{

    private final HistoryRepository historyRepository;

    @Override
    public boolean historyExist(Long historyId) {
        return historyRepository.existsById(historyId);
    }
}
