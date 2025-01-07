package com.clokey.server.domain.model;

import com.clokey.server.domain.model.enums.MemberStatus;
import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.domain.model.mapping.MemberLike;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class History extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate historyDate; //기록 일자

    private String historyImageUrl; //기록사진url

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL)
    private List<MemberLike> memberLikeList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
