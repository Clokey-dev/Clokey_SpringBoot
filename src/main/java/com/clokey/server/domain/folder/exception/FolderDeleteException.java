package com.clokey.server.domain.folder.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class FolderDeleteException extends GeneralException {
    public FolderDeleteException(BaseErrorCode code) {
        super(code);
    }
}

