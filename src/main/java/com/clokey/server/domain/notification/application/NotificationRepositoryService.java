package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;

public interface NotificationRepositoryService {

    void save(ClokeyNotification clokeyNotification);

}
