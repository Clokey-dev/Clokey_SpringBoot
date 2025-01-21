package com.clokey.server.domain.cloth.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class ClothException extends GeneralException {

    public ClothException(BaseErrorCode code) {
        super(code);
    }
}
