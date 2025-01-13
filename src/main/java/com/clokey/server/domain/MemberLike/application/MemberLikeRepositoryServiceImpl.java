package com.clokey.server.domain.MemberLike.application;

import com.clokey.server.domain.MemberLike.dao.MemberLikeRepository;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.mapping.MemberLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLikeRepositoryServiceImpl implements MemberLikeRepositoryService {

    private final MemberLikeRepository memberLikeRepository;
    private final HistoryRepositoryService historyRepositoryService;
    private final MemberRepositoryService memberRepositoryService;

    @Override
    public int countLikesOfHistory(Long historyId) {
        return memberLikeRepository.countByHistory_Id(historyId);
    }

    @Override
    public boolean memberLikedHistory(Long memberId, Long historyId) {
        return memberLikeRepository.existsByMember_IdAndHistory_Id(memberId,historyId);
    }

    @Override
    public void deleteLike(Long memberId, Long historyId) {
        memberLikeRepository.deleteByMemberIdAndHistoryId(memberId,historyId);
    }

    @Override
    public void saveLike(Long memberId, Long historyId) {
        MemberLike memberLike = MemberLike.builder()
                .history(historyRepositoryService.getHistoryById(historyId).get())
                .member(memberRepositoryService.getMember(memberId).get())
                .build();
    }

    @Override
    public void changeLike(Long memberId, Long historyId, boolean isLiked) {
        if(isLiked) {
            deleteLike(memberId,historyId);
            return;
        }
        saveLike(memberId,historyId);
    }
}
