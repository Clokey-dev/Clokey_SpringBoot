package com.clokey.server.domain.notification.exception;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.exception.GeneralException;

public class NotificationException extends GeneralException {

    public NotificationException(BaseErrorCode code) {
        super(code);
    }
}

