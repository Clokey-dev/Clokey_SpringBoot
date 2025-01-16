package com.clokey.server.domain.history.application;

import com.clokey.server.domain.model.entity.Comment;
import com.clokey.server.domain.model.entity.History;

import java.util.List;
import java.util.Optional;

public interface HistoryRepositoryService {

    boolean historyExist(Long historyId);

    Optional<History> getHistoryById(Long historyId);

    boolean isPublic(Long historyId);

    List<History> getMemberHistoryByYearMonth(Long memberId, String yearMonth);

    List<String> getFirstImageUrlsOfHistory(List<History> histories);
}
