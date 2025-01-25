package com.clokey.server.domain.history.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class HistoryException extends GeneralException {

    public HistoryException(BaseErrorCode code) {
        super(code);
    }
}
