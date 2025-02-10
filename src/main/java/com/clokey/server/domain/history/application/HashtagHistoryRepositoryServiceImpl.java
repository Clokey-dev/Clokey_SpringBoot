package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.repository.HashtagHistoryRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void save(HashtagHistory hashtagHistory) {
        hashtagHistoryRepository.save(hashtagHistory);
    }

    public void addHashtagHistory(Hashtag hashtag, History history) {
        // HashtagHistory 엔티티 생성 및 저장
        HashtagHistory hashtagHistory = HashtagHistory.builder()
                .hashtag(hashtag)
                .history(history)
                .build();

        hashtagHistoryRepository.save(hashtagHistory);
    }

    public void deleteHashtagHistory(Hashtag hashtag, History history) {
        hashtagHistoryRepository.deleteByHashtagAndHistory(hashtag, history);
    }

    @Override
    public void deleteAllByHistoryId(Long historyId) {
        hashtagHistoryRepository.deleteAllByHistoryId(historyId);
    }

    @Override
    public List<String> findHashtagNamesByHistoryId(Long historyId) {
        return hashtagHistoryRepository.findHashtagNamesByHistoryId(historyId);
    }

    @Override
    public List<Long> findTop3HashtagIdsByMemberIdOrderByHistoryDateDesc(Long memberId) {
        return hashtagHistoryRepository.findTop3HashtagIdsByMemberIdOrderByHistoryDateDesc(memberId);
    }

    @Override
    public String findLatestTaggedHashtag(Long memberId) {
        return hashtagHistoryRepository.findLatestTaggedHashtag(memberId)
                .orElse(null);
    }

    @Override
    public Long findHistoryIdByHashtagName(String hashtagName) {
        return hashtagHistoryRepository.findHistoryIdByHashtagName(hashtagName)
                .orElse(null);
    }
}
