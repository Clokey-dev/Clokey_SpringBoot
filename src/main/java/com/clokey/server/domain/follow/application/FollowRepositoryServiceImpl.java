package com.clokey.server.domain.follow.application;

import com.clokey.server.domain.follow.dao.FollowRepository;
import com.clokey.server.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowRepositoryServiceImpl implements FollowRepositoryService{

    private final FollowRepository followRepository;

    @Override
    public boolean isFriends(Long memberId1, Long memberId2) {

        boolean member1FollowsMember2 = followRepository.existsByFollowing_IdAndFollowed_Id(memberId1,memberId2);
        boolean member2FollowsMember2 = followRepository.existsByFollowing_IdAndFollowed_Id(memberId2,memberId1);

        return member1FollowsMember2 && member2FollowsMember2;
    }
}
