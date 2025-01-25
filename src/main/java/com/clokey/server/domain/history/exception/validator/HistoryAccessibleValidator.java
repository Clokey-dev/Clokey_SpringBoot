package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.history.domain.repository.HistoryRepository;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HistoryAccessibleValidator {

    private final HistoryRepositoryService historyRepositoryService;
    private final MemberRepositoryService memberRepositoryService;

    public void validateHistoryAccessOfMember(Long historyId, Long memberId) {
        History history = historyRepositoryService.findById(historyId);

        //접근 권한 확인 - 나의 기록이 아니고 비공개일 경우 접근 불가.
        boolean isPrivate = history.getVisibility().equals(Visibility.PRIVATE);
        boolean isNotMyHistory = !history.getMember().getId().equals(memberId);

        if (isPrivate && isNotMyHistory) {
            throw new GeneralException(ErrorStatus.NO_PERMISSION_TO_ACCESS_HISTORY);
        }
    }

    public void validateMemberAccessOfMember(Long memberToBeQueried, Long memberRequestingQuery) {

        //접근 권한 확인 - 내 자신을 확인하는 것도 아니고 비공개인 경우.
        boolean selfQuery = memberToBeQueried.equals(memberRequestingQuery);
        boolean isPrivate = memberRepositoryService.findMemberById(memberToBeQueried)
                .getVisibility()
                .equals(Visibility.PRIVATE);

        if(!selfQuery && isPrivate) {
            throw new GeneralException(ErrorStatus.NO_PERMISSION_TO_ACCESS_HISTORY);
        }

    }

}
