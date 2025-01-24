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

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    @Modifying
    @Query("UPDATE History h SET h.likes = h.likes + 1 WHERE h.id = :historyId")
    void incrementLikes(Long historyId);

    // Likes 감소
    @Modifying
    @Query("UPDATE History h SET h.likes = h.likes - 1 WHERE h.id = :historyId")
    void decrementLikes(Long historyId);

    @Override
    public History findById(Long historyId) {
        return historyRepository.findById(historyId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_HISTORY));
    }
}
