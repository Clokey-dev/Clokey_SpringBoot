package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.domain.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowRepositoryServiceImpl implements FollowRepositoryService{

    private final FollowRepository followRepository;

    @Override
    public List<Boolean> checkFollowingStatus(Long followedId, List<Member> members) {
        return followRepository.checkFollowingStatus(followedId,members);
    }
}
