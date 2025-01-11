package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HistoryAccessibleValidator {

    private final HistoryRepositoryService historyRepositoryService;

    public void validateHistoryAccess(Long historyId, Long memberId) {
        Optional<History> history = historyRepositoryService.getHistoryById(historyId);

        //접근 권한 확인
        boolean isPrivate = history.get().getVisibility().equals(Visibility.PRIVATE);
        boolean isNotMyHistory = !history.get().getMember().getId().equals(memberId);

        if (isPrivate && isNotMyHistory) {
            throw new GeneralException(ErrorStatus.NO_PERMISSION_TO_ACCESS_HISTORY);
        }
    }
}
