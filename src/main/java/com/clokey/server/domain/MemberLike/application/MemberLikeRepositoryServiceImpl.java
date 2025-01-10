package com.clokey.server.domain.MemberLike.application;

import com.clokey.server.domain.MemberLike.dao.MemberLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLikeRepositoryServiceImpl implements MemberLikeRepositoryService {

    private final MemberLikeRepository memberLikeRepository;

    @Override
    public int countLikesOfHistory(Long historyId) {
        return memberLikeRepository.countByHistory_Id(historyId);
    }

    @Override
    public boolean memberLikedHistory(Long memberId, Long historyId) {
        return memberLikeRepository.existsByMember_IdAndHistory_Id(memberId,historyId);
    }
}
