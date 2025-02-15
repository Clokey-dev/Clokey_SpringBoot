package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;

import java.util.List;

public interface HashtagHistoryRepositoryService {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);

    void save(HashtagHistory hashtagHistory);

    void addHashtagHistory(Hashtag hashtag, History history);

    void deleteHashtagHistory(Hashtag hashtag, History history);

    void deleteAllByHistoryId(Long historyId);

    List<String> findHashtagNamesByHistoryId(Long historyId);

    void deleteAllByHistoryIds(List<Long> historyIds);
}
