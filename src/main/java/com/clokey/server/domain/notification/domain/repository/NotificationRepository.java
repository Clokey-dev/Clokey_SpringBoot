package com.clokey.server.domain.notification.domain.repository;

import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<ClokeyNotification, Long> {
}
