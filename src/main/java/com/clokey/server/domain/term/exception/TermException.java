package com.clokey.server.domain.term.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class TermException extends GeneralException {
    public TermException(BaseErrorCode code) {
        super(code);
    }
}
