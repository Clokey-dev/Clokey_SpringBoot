package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.HistoryCloth;
import com.clokey.server.domain.history.domain.repository.HistoryClothRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class HistoryClothRepositoryServiceImpl implements HistoryClothRepositoryService{

    private final HistoryClothRepository historyClothRepository;

    @Override
    public void save(HistoryCloth historyCloth) {
        historyClothRepository.save(historyCloth);
    }
}
