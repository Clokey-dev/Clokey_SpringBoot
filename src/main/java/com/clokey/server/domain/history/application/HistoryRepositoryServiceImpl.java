package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.dao.HistoryRepository;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;
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

    //validation 처리할 때 해결.
    @Override
    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).get();
    }

    @Override
    public boolean isPublic(Long historyId) {
        Visibility visibility = historyRepository.findById(historyId)
                .get()
                .getVisibility();
        return visibility.equals(Visibility.PUBLIC);
    }

}
