package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
