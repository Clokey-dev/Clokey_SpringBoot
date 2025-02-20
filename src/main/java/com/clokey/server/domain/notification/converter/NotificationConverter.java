package com.clokey.server.domain.notification.converter;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;

public class NotificationConverter {

    public static NotificationResponseDTO.GetNotificationResult toNotificationResult(
            List<ClokeyNotification> notifications, Pageable pageable) {

        List<NotificationResponseDTO.NotificationResult> notificationResults = notifications.stream()
                .map(NotificationConverter::convertToNotificationResult)
                .collect(Collectors.toList());

        return NotificationResponseDTO.GetNotificationResult.builder()
                .notificationResults(notificationResults)
                .totalPage(pageable.getPageNumber() + 1)
                .totalElements(notificationResults.size())
                .isFirst(pageable.getPageNumber() == 0)
                .isLast(notificationResults.size() < pageable.getPageSize())
                .build();
    }

    private static NotificationResponseDTO.NotificationResult convertToNotificationResult(ClokeyNotification notification) {
        return NotificationResponseDTO.NotificationResult.builder()
                .notificationId(notification.getId())
                .content(notification.getContent())
                .notificationImageUrl(notification.getNotificationImageUrl())
                .redirectInfo(notification.getRedirectInfo())
                .redirectType(notification.getRedirectType())
                .isRead(notification.getReadStatus() == ReadStatus.READ)
                .createdAt(LocalDate.from(notification.getCreatedAt()))
                .build();
    }
}
