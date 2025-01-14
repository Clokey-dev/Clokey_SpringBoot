package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HistoryLikedValidator {

    private final MemberLikeRepositoryService memberLikeRepositoryService;

    public void validateIsLiked(Long historyId, Long memberId, boolean isLiked) {

        boolean isValid = memberLikeRepositoryService.memberLikedHistory(memberId,historyId) == isLiked;

        if (!isValid) {
            throw new GeneralException(ErrorStatus.IS_LIKED_INVALID);
        }
    }

}
