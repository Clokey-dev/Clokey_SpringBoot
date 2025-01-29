package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.repository.HistoryRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryRepositoryServiceImpl implements HistoryRepositoryService {

    private final HistoryRepository historyRepository;

    @Override
    public List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth){
        return historyRepository.findHistoriesByMemberAndYearMonth(memberId,yearMonth);
    }


    @Override
    public void incrementLikes(Long historyId){
        historyRepository.incrementLikes(historyId);
    }

    @Override
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

    @Override
    public History save(History history) {
        return historyRepository.save(history);
    }

    @Override
    public boolean checkHistoryExistOfDate(LocalDate date, Long memberId) {
        return historyRepository.existsByHistoryDateAndMember_Id(date,memberId);
    }

    @Override
    public History getHistoryOfDate(LocalDate date, Long memberId) {
        return historyRepository.findByHistoryDateAndMember_Id(date,memberId)
                .orElseThrow(()-> new DatabaseException(ErrorStatus.NO_HISTORY_FOR_DATE));
    }

    @Override
    public void deleteById(Long historyId) {
        historyRepository.deleteById(historyId);
    }
}
