package com.clokey.server.domain.recommendation.domain.entity;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.model.entity.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recommendation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long contentId; // closetId, calendarId

    @Column(length = 500)
    private String imageUrl;

    @Column
    private Double temperature;

    @Column(length = 1000)
    private String clothesIds; // 추천된 옷 ID 리스트 (JSON 형태로 저장 가능)

    @Column(length = 500)
    private String hashtag;

    @Column(length = 500)
    private String subTitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NewsType newsType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}

