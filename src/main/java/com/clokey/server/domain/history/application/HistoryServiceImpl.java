package com.clokey.server.domain.history.application;

import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.history.dao.HistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryRepository historyRepository;

    @Override
    public void changeLike(Long memberId, Long historyId, boolean isLiked) {
        if(isLiked) {
            historyRepository.decrementLikes(historyId);
            memberLikeRepositoryService.deleteLike(memberId,historyId);
        } else {
            historyRepository.incrementLikes(historyId);
            memberLikeRepositoryService.saveLike(memberId,historyId);
        }
    }
}
