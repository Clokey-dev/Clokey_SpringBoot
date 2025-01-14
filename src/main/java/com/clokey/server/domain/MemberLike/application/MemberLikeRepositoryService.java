package com.clokey.server.domain.MemberLike.application;

public interface MemberLikeRepositoryService {

    int countLikesOfHistory(Long historyId);

    boolean memberLikedHistory(Long memberId, Long historyId);

    void deleteLike(Long memberId, Long historyId);

    void saveLike(Long memberId, Long historyId);
}
