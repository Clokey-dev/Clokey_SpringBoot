package com.clokey.server.domain.category.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class CategoryException extends GeneralException {

    public CategoryException(BaseErrorCode code) {super(code);}
}
