package com.clokey.server.domain.history.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class HashtagException extends GeneralException {

    public HashtagException(BaseErrorCode code) {
        super(code);
    }
}
