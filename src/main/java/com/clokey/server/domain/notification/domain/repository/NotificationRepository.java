package com.clokey.server.domain.notification.domain.repository;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<ClokeyNotification, Long> {

    boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus);

    void deleteByMemberId(Long memberId);

    @Modifying
    @Query("DELETE FROM ClokeyNotification c WHERE c.id IN :clokeyNotificationIds")
    void deleteByClokeyNotificationIds(@Param("clokeyNotificationIds") List<Long> clokeyNotificationIds);
}
