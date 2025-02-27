package com.clokey.server.domain.notification.application;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import com.clokey.server.domain.notification.domain.repository.NotificationRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;

@Service
@RequiredArgsConstructor
public class NotificationRepositoryServiceImpl implements NotificationRepositoryService {

    private final NotificationRepository notificationRepository;

    @Override
    public void save(ClokeyNotification clokeyNotification) {
        notificationRepository.save(clokeyNotification);
    }

    @Override
    public boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus) {
        return notificationRepository.existsByMemberIdAndReadStatus(memberId, readStatus);
    }

    @Override
    public boolean existsById(Long notificationId) {
        return notificationRepository.existsById(notificationId);
    }

    @Override
    public ClokeyNotification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_NOTIFICATION));
    }

    @Override
    public void deleteBymemberId(Long memberId) {
        notificationRepository.deleteByMemberId(memberId);
    }

    @Override
    public void deleteByClokeyNotificationIds(List<Long> clokeyNotificationIds) {
        notificationRepository.deleteByClokeyNotificationIds(clokeyNotificationIds);
    }

    @Override
    public List<ClokeyNotification> findNotificationsByMemberId(Long memberId, Pageable pageable) {
        return notificationRepository.findNotificationsByMemberId(memberId, pageable);
    }

    @Override
    public void readAllByMemberId(Long memberId) {
        notificationRepository.readAllByMemberId(memberId);
    }
}
