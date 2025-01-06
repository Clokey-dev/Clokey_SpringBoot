package com.clokey.server.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"following_user_id", "followed_user_id"}))
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followId; // 팔로우 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", nullable = false) // 팔로잉 유저 ID
    private Member following;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id", nullable = false) // 팔로우당한 유저 ID
    private Member followed;

    public Follow(Member following, Member followed) {
        this.following = following;
        this.followed = followed;
    }
}

