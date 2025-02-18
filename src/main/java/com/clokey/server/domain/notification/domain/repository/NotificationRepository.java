package com.clokey.server.domain.notification.domain.repository;

import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<ClokeyNotification, Long> {

    boolean existsByMemberIdAndReadStatus(Long memberId, ReadStatus readStatus);

    @Query("SELECT n FROM ClokeyNotification n WHERE n.member.id = :memberId ORDER BY CASE WHEN n.readStatus = 'NOT_READ' THEN 0 ELSE 1 END, n.createdAt DESC")
    List<ClokeyNotification> findNotificationsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("UPDATE ClokeyNotification c SET c.readStatus = 'READ' WHERE c.member.id = :memberId AND c.readStatus = 'NOT_READ'")
    void readAllByMemberId(@Param("memberId") Long memberId);
}
