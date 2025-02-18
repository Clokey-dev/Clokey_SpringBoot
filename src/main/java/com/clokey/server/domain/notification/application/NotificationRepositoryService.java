package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationRepositoryService {

    void save(ClokeyNotification clokeyNotification);

    boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus);

    boolean existsById(Long notificationId);

    ClokeyNotification findById(Long notificationId);

    List<ClokeyNotification> findNotificationsByMemberId(Long memberId, Pageable pageable);

    void readAllByMemberId(Long memberId);
}
