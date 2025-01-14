package com.clokey.server.domain.HashtagHistory.application;

import com.clokey.server.domain.HashtagHistory.dao.HashtagHistoryRepository;
import com.clokey.server.domain.hashtag.dao.HashtagRepository;
import com.clokey.server.domain.model.Hashtag;
import com.clokey.server.domain.model.mapping.HashtagHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagHistoryRepositoryServiceImpl implements HashtagHistoryRepositoryService{

    private final HashtagHistoryRepository hashtagHistoryRepository;
    private final HashtagRepository hashtagRepository;

    @Override
    public boolean historyHashtagExist(Long historyId) {
        return hashtagHistoryRepository.existsByHistory_Id(historyId);
    }

    @Override
    public List<String> getHistoryHashtags(Long historyId) {
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistory_Id(historyId);

        return hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .map(Hashtag::getName)
                .toList();
    }
}
