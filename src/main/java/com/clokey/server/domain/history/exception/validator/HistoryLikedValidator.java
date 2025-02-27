package com.clokey.server.domain.history.exception.validator;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.history.application.MemberLikeRepositoryService;
import com.clokey.server.domain.history.exception.HistoryException;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class HistoryLikedValidator {

    private final MemberLikeRepositoryService memberLikeRepositoryService;

    public void validateIsLiked(Long historyId, Long memberId, boolean isLiked) {

        boolean isValid = memberLikeRepositoryService.existsByMember_IdAndHistory_Id(memberId, historyId) == isLiked;

        if (!isValid) {
            throw new HistoryException(ErrorStatus.IS_LIKED_INVALID);
        }
    }

}
