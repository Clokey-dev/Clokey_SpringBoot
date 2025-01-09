package com.clokey.server.domain.follow.application;

public interface FollowRepositoryService {

    //서로 맞팔인지 확인합니다.
    boolean isFriends(Long memberId1, Long memberId2);

}
