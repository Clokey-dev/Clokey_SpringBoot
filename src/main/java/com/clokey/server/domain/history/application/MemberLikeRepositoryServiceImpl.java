package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.MemberLike;
import com.clokey.server.domain.history.domain.repository.MemberLikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLikeRepositoryServiceImpl implements MemberLikeRepositoryService{

    private final MemberLikeRepository memberLikeRepository;

    @Override
    public int countByHistory_Id(Long historyId) {
        return memberLikeRepository.countByHistory_Id(historyId);
    }

    @Override
    public boolean existsByMember_IdAndHistory_Id(Long memberId, Long historyId) {
        return memberLikeRepository.existsByMember_IdAndHistory_Id(memberId,historyId);
    }

    @Override
    public void deleteByMember_IdAndHistory_Id(Long memberId, Long historyId) {
        memberLikeRepository.deleteByMember_IdAndHistory_Id(memberId,historyId);
    }

    @Override
    public void save(MemberLike memberLike) {
        memberLikeRepository.save(memberLike);
    }
}
