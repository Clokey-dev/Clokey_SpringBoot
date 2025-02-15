package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import com.clokey.server.domain.notification.domain.repository.NotificationRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationRepositoryServiceImpl implements NotificationRepositoryService{

    private final NotificationRepository notificationRepository;

    @Override
    public void save(ClokeyNotification clokeyNotification) {
        notificationRepository.save(clokeyNotification);
    }

    @Override
    public boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus) {
        return notificationRepository.existsByMemberIdAndReadStatus(memberId,readStatus);
    }

    @Override
    public boolean existsById(Long notificationId) {
        return notificationRepository.existsById(notificationId);
    }

    @Override
    public ClokeyNotification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_NOTIFICATION));
    }

    @Override
    public void deleteBymemberId(Long memberId) {
        notificationRepository.deleteByMemberId(memberId);
    }

    @Override
    public void deleteByClokeyNotificationIds(List<Long> clokeyNotificationIds){
        notificationRepository.deleteByClokeyNotificationIds(clokeyNotificationIds);
    }

}
