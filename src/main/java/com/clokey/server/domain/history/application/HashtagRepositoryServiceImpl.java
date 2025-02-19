package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.repository.HashtagRepository;
import com.clokey.server.domain.history.exception.HashtagException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class HashtagRepositoryServiceImpl implements HashtagRepositoryService {

    private final HashtagRepository hashtagRepository;

    @Override
    public boolean existByName(String name) {
        return hashtagRepository.findByName(name).isPresent();
    }

    @Override
    public Hashtag findByName(String name) {
        return hashtagRepository.findByName(name).orElseThrow(() -> new HashtagException(ErrorStatus.NO_SUCH_HASHTAG_NAME));
    }

    @Override
    public void save(Hashtag hashtag) {
        hashtagRepository.save(hashtag);
    }

    @Override
    public List<Hashtag> findHashtagsByNames(List<String> names) {
        return hashtagRepository.findHashtagsByNames(names);
    }

    @Override
    public String findRandomUnusedHashtag(Long memberId) {
        return hashtagRepository.findRandomUnusedHashtag(memberId)
                .orElse(null);
    }
}
