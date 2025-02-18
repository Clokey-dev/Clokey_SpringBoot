package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashtagRepositoryService {

    boolean existByName(String name);

    Hashtag findByName(String name);

    void save(Hashtag hashtag);

    List<Hashtag> findHashtagsByNames(List<String> names);

    String findRandomUnusedHashtag(Long memberId);
}
