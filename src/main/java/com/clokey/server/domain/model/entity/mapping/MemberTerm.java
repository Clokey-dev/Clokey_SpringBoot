package com.clokey.server.domain.model.entity.mapping;

import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.entity.Term;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberTerm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id",nullable = false)
    private Term term;
}
