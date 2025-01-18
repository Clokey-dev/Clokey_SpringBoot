package com.clokey.server.domain.model.entity;

import com.clokey.server.domain.model.entity.enums.NotificationType;
import com.clokey.server.domain.model.entity.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 50)
    private String content;

    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'NOT_READ'",nullable = false)
    private ReadStatus readStatus;
}
