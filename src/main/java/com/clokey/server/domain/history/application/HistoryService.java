package com.clokey.server.domain.history.application;

public interface HistoryService {

    void changeLike(Long memberId, Long historyId, boolean isLiked);
}
