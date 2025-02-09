package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import com.clokey.server.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationRepositoryServiceImpl implements NotificationRepositoryService{

    private NotificationRepository notificationRepository;

    @Override
    public void save(ClokeyNotification clokeyNotification) {
        notificationRepository.save(clokeyNotification);
    }

    @Override
    public boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus) {
        return notificationRepository.existsByMemberIdAndReadStatus(memberId,readStatus);
    }
}
