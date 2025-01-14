package com.clokey.server.domain.member.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class MemberException extends GeneralException {

    public MemberException(BaseErrorCode code) {
        super(code);
    }
}


