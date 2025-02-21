package com.clokey.server.domain.folder.domain.entity;

import jakarta.persistence.*;

import lombok.*;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Folder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id",nullable = false)
    private Member member;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Long itemCount = 0L;

    public void rename(String newName) {
        this.name = newName;
    }
    public void increaseItemCount() {
        this.itemCount++;
    }
    public void decreaseItemCount() {
        this.itemCount--;
    }
    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }
}
