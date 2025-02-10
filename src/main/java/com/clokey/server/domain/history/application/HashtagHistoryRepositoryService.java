package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;

import java.util.List;
import java.util.Optional;

public interface HashtagHistoryRepositoryService {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);

    void save(HashtagHistory hashtagHistory);

    void addHashtagHistory(Hashtag hashtag, History history);

    void deleteHashtagHistory(Hashtag hashtag, History history);

    void deleteAllByHistoryId(Long historyId);

    List<String> findHashtagNamesByHistoryId(Long historyId);

    List<Long> findTop3HashtagIdsByMemberIdOrderByHistoryDateDesc(Long memberId);

    String findLatestTaggedHashtag(Long memberId);

    Long findHistoryIdByHashtagName(String hashtagName);
}
