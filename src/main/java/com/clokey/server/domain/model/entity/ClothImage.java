package com.clokey.server.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClothImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl; // 옷 이미지 URL

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id", nullable = false)
    private Cloth cloth;
}
