package com.clokey.server.global.error.exception;

import com.clokey.server.global.error.code.BaseErrorCode;

public class DatabaseException extends GeneralException {

    public DatabaseException(BaseErrorCode code) {
        super(code);
    }
}
