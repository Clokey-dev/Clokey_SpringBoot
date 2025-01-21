package com.clokey.server.domain.folder.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class FolderException extends GeneralException {
    public FolderException(BaseErrorCode code) {
        super(code);
    }
}

