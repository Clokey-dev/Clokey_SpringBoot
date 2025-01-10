package com.clokey.server.domain.MemberLike.application;

public interface MemberLikeRepositoryService {

    int countLikesOfHistory(Long historyId);

    boolean memberLikedHistory(Long memberId, Long historyId);
}
