package com.clokey.server.domain.notification.application;

import org.springframework.data.domain.Pageable;

import java.util.List;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;

public interface NotificationRepositoryService {

    void save(ClokeyNotification clokeyNotification);

    boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus);

    boolean existsById(Long notificationId);

    ClokeyNotification findById(Long notificationId);

    void deleteBymemberId(Long memberId);

    void deleteByClokeyNotificationIds(List<Long> clokeyNotificationIds);

    List<ClokeyNotification> findNotificationsByMemberId(Long memberId, Pageable pageable);

    void readAllByMemberId(Long memberId);
}
