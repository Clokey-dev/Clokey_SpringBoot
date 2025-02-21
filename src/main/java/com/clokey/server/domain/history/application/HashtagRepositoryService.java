package com.clokey.server.domain.history.application;

import java.util.List;

import com.clokey.server.domain.history.domain.entity.Hashtag;

public interface HashtagRepositoryService {

    boolean existByName(String name);

    Hashtag findByName(String name);

    void save(Hashtag hashtag);

    List<Hashtag> findHashtagsByNames(List<String> names);

    String findRandomUnusedHashtag(Long memberId);
}
