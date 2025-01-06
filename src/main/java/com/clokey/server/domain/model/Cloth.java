package com.clokey.server.domain.model;

import com.clokey.server.domain.model.mapping.HistoryCloth;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import com.clokey.server.domain.model.enums.Visibility;

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
    @Column(nullable = false, length = 20) // 공개 범위
    private Visibility visibility;

    @Column(nullable = false) // 상한 온도
    private int tempUpperBound;

    @Column(nullable = false) // 하한 온도
    private int tempLowerBound;

    @Min(0)
    @Max(5)
    @Column(nullable = false) // 옷 두계 0~5레벨
    private int thicknessLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_cloth_id", nullable = false)
    private HistoryCloth historyCloth;
}
