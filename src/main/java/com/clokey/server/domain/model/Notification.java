package com.clokey.server.domain.model;

import com.clokey.server.domain.model.enums.ReadStatus;
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
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, length = 50)
    private String content;

    //default 읽지 않음 추가해야함. nullable = false 를 해야함.
    private ReadStatus readStatus;
}
