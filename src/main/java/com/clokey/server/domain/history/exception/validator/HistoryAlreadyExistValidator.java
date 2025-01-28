package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.HistoryException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class HistoryAlreadyExistValidator {

    private final HistoryRepositoryService historyRepositoryService;

    public void validate(Long memberId, String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean isNotValid = historyRepositoryService.checkHistoryExistOfDate(LocalDate.parse(date, formatter),memberId);

        if(isNotValid) {
            throw new HistoryException(ErrorStatus.HISTORY_ALREADY_EXIST_FOR_DATE);
        }
    }
}
