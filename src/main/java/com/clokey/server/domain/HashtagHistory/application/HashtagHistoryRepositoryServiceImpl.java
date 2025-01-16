package com.clokey.server.domain.HashtagHistory.application;

import com.clokey.server.domain.model.repository.HashtagHistoryRepository;
import com.clokey.server.domain.model.repository.HashtagRepository;
import com.clokey.server.domain.model.entity.Hashtag;
import com.clokey.server.domain.model.entity.mapping.HashtagHistory;
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
