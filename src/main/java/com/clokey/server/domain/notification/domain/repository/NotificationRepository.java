package com.clokey.server.domain.notification.domain.repository;

import com.clokey.server.domain.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
