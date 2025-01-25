package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.repository.HistoryRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryRepositoryServiceImpl implements HistoryRepositoryService {

    private final HistoryRepository historyRepository;

    public List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth){
        return historyRepository.findHistoriesByMemberAndYearMonth(memberId,yearMonth);
    }

    @Modifying
    public void incrementLikes(Long historyId){
        historyRepository.incrementLikes(historyId);
    }

    @Modifying
    public void decrementLikes(Long historyId){
        historyRepository.decrementLikes(historyId);
    }

    @Override
    public History findById(Long historyId) {
        return historyRepository.findById(historyId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_HISTORY));
    }

    @Override
    public boolean existsById(Long historyId) {
        return historyRepository.existsById(historyId);
    }
}
