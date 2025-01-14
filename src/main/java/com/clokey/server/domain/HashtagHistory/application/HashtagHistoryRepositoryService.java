package com.clokey.server.domain.HashtagHistory.application;

import java.util.List;

public interface HashtagHistoryRepositoryService {

    boolean historyHashtagExist(Long historyId);

    List<String> getHistoryHashtags(Long historyId);

}
