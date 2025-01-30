package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryCloth;
import com.clokey.server.domain.history.domain.repository.HistoryClothRepository;
import com.clokey.server.domain.history.domain.repository.HistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HistoryClothRepositoryServiceImpl implements HistoryClothRepositoryService{

    private final HistoryClothRepository historyClothRepository;
    @Override
    public void save(History history , Cloth cloth) {
        cloth.increaseWearNum();
        historyClothRepository.save(HistoryCloth.builder()
                        .history(history)
                        .cloth(cloth)
                        .build());
    }

    @Override
    public void delete(History history, Cloth cloth) {
        cloth.decreaseWearNum();
        historyClothRepository.deleteByHistoryAndCloth(history,cloth);
    }

    @Override
    public List<Long> findClothIdsByHistoryId(Long historyId){
        return historyClothRepository.findClothIdsByHistoryId(historyId);
    }

    @Override
    public void deleteAllByClothId(Long clothId){
        historyClothRepository.deleteAllByClothId(clothId);
    }

    @Override
    public List<Cloth> findAllClothByHistoryId(Long historyId) {
        return historyClothRepository.findAllClothsByHistoryId(historyId);
    }

    @Override
    public void deleteAllByHistoryId(Long historyId) {
        historyClothRepository.deleteAllByHistoryId(historyId);
    }
}
