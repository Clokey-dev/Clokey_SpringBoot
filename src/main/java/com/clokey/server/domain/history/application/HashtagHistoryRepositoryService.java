package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.HashtagHistory;

import java.util.List;

public interface HashtagHistoryRepositoryService {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);
}
