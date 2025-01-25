package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Hashtag;

public interface HashtagRepositoryService {

    boolean existByName(String name);

    Hashtag findByName(String name);
}
