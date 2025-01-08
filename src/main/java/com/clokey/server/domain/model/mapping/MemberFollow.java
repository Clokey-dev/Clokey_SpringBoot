package com.clokey.server.domain.model.mapping;

import com.clokey.server.domain.model.BaseEntity;
import com.clokey.server.domain.model.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"following_user_id", "followed_user_id"}))
public class MemberFollow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", nullable = false) // 팔로잉 유저 ID
    private Member following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id", nullable = false) // 팔로우당한 유저 ID
    private Member followed;
}

