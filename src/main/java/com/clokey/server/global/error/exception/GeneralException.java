package com.clokey.server.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}
