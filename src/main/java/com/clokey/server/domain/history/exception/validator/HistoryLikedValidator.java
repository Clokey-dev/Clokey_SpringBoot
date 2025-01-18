package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.model.repository.MemberLikeRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryLikedValidator {

    private final MemberLikeRepository memberLikeRepository;

    public void validateIsLiked(Long historyId, Long memberId, boolean isLiked) {

        boolean isValid = memberLikeRepository.existsByMember_IdAndHistory_Id(memberId,historyId) == isLiked;

        if (!isValid) {
            throw new GeneralException(ErrorStatus.IS_LIKED_INVALID);
        }
    }

}
