package com.clokey.server.domain.recommendation.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class RecommendException extends GeneralException {

    public RecommendException(BaseErrorCode code) {
        super(code);
    }
}
