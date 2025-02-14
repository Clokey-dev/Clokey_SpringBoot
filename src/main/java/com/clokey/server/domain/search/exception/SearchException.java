package com.clokey.server.domain.search.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class SearchException extends GeneralException {

    public SearchException(BaseErrorCode code) {super(code);}
}
