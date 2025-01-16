package com.clokey.server.domain.model;

import com.clokey.server.domain.model.enums.Season;
import com.clokey.server.domain.model.enums.ThicknessLevel;
import com.clokey.server.domain.model.mapping.HistoryCloth;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import com.clokey.server.domain.model.enums.Visibility;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
public class Cloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int wearNum;

    @CreatedDate // 등록 날짜를 자동으로 기록
    @Column(nullable = false, updatable = false)
    private LocalDate regDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempUpperBound;

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempLowerBound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThicknessLevel thicknessLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(nullable = true)
    private String clothUrl;

    @Column(nullable = true)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
