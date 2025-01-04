package com.clokey.server.domain.model;

import com.clokey.server.domain.model.enums.Gender;
import com.clokey.server.domain.model.enums.MemberStatus;
import com.clokey.server.domain.model.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30) //이름
    private String name;

    @Column(nullable = false, unique = true) //이메일
    private String email;

    @Column(nullable = false, length = 30, unique=true) //닉네임
    private String nickname;

    @Column(nullable = false, length = 30, unique=true) //사용자 지정 아이디
    private String userId;

    @Enumerated(EnumType.STRING) //성별
    @Column(columnDefinition = "VARCHAR(10)")
    private Gender gender;

    private LocalDate birthDate; //생년월일

    @Enumerated(EnumType.STRING) //가입종류
    private SocialType socialType;

    private String profileImageUrl; //프로필사진url

    @Enumerated(EnumType.STRING) //활성화여부
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private MemberStatus status;

    private LocalDate inactiveDate; //비활성화 일자

    // 소셜 로그인이라서 비밀번호는 안했음


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Follow> followList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Category> categoryList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Cloth> clothesList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Record> recordList = new ArrayList<>();
}
