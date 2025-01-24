package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.repository.HashtagHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HashtagHistoryRepositoryServiceImpl implements HashtagHistoryRepositoryService{

    private final HashtagHistoryRepository hashtagHistoryRepository;


    @Override
    public boolean existsByHistory_Id(Long historyId) {
        return hashtagHistoryRepository.existsByHistory_Id(historyId);
    }

    @Override
    public List<HashtagHistory> findByHistory_Id(Long historyId) {
        return hashtagHistoryRepository.findByHistory_Id(historyId);
    }
}
