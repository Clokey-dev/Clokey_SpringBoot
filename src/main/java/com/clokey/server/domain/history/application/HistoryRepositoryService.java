package com.clokey.server.domain.history.application;

public interface HistoryRepositoryService {

    boolean historyExist(Long historyId);

    boolean isPublic(Long historyId);
}
