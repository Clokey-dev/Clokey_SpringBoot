package com.clokey.server.domain.notification.domain.entity;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.model.entity.enums.NotificationType;
import com.clokey.server.domain.model.entity.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClokeyNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String content;

    // 우선 그냥 두는데 픽토그램 이미지는 프론트에서 알아서 하는건지...?
    private String notificationImageUrl;

    //ex) historyId, clokeyId
    @Column(nullable = false)
    private Object redirectInfo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'NOT_READ'",nullable = false)
    private ReadStatus readStatus;

    public void readNotification() {
        this.readStatus = ReadStatus.READ;
    }
}
