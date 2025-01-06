package com.clokey.server.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50) // 옷 이름
    private String name;

    @Column(nullable = false)
    private int wearNum; // 옷 착용 횟수

    @Column(nullable = false)
    private LocalDate regDate; // 등록 날짜

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10) // 공개 범위
    private Visibility visibility;

    @Column(nullable = false) // 상한 온도
    private int tempUpperBound;

    @Column(nullable = false) // 하한 온도
    private int tempLowerBound;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
