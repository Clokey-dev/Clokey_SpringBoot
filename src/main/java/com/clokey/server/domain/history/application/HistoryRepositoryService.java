package com.clokey.server.domain.history.application;

import com.clokey.server.domain.model.History;

public interface HistoryRepositoryService {

    boolean historyExist(Long historyId);

    History getHistoryById(Long historyId);

    boolean isPublic(Long historyId);
}
