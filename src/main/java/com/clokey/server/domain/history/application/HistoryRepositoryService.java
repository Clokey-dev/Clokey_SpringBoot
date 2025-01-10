package com.clokey.server.domain.history.application;

import com.clokey.server.domain.model.History;

import java.util.List;

public interface HistoryRepositoryService {

    boolean historyExist(Long historyId);

    History getHistoryById(Long historyId);

    boolean isPublic(Long historyId);

    List<History> getMemberHistoryByYearMonth(Long memberId, String yearMonth);

    List<String> getFirstImageUrlsOfHistory(List<History> histories);
}
