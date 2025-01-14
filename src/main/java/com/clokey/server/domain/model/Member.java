package com.clokey.server.domain.model;

import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.domain.model.mapping.MemberTerm;
import com.clokey.server.domain.model.enums.MemberStatus;
import com.clokey.server.domain.model.enums.SocialType;
import com.clokey.server.domain.model.mapping.MemberLike;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 30)
    private String nickname;

    @Column(unique=true)
    private String clokeyId;

    @Column(length = 100) //한줄 소개
    private String bio;

    @Enumerated(EnumType.STRING) //가입종류
    @Column(nullable = false)
    private SocialType socialType;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING) //활성화여부
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'" , nullable = false)
    private MemberStatus status;

    private LocalDate inactiveDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'PUBLIC'",nullable = false) // 공개 범위
    private Visibility visibility;

    //필요한 양방향 매핑을 제외하고 삭제해주세요.
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberTerm> memberTermList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberLike> memberLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Cloth> clothList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<History> historyList = new ArrayList<>();
}
