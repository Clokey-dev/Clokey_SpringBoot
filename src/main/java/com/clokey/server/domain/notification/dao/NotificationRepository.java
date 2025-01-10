package com.clokey.server.domain.notification.dao;

import com.clokey.server.domain.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
