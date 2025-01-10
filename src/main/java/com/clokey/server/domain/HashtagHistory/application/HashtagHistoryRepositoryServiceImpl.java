package com.clokey.server.domain.HashtagHistory.application;

import com.clokey.server.domain.HashtagHistory.dao.HashtagHistoryRepository;
import com.clokey.server.domain.model.Hashtag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashtagHistoryRepositoryServiceImpl implements HashtagHistoryRepositoryService{

    private final HashtagHistoryRepository hashtagHistoryRepository;

    @Override
    public boolean historyHashtagExist(Long historyId) {
        return hashtagHistoryRepository.existsByHistory_Id(historyId);
    }

    @Override
    public List<String> getHistoryHashtags(Long historyId) {
        List<Hashtag> hashtags = hashtagHistoryRepository.findByHistory_Id(historyId);

        return hashtags.stream()
                .map(Hashtag::getName)
                .toList();
    }
}
