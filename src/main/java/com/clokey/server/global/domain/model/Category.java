package com.clokey.server.global.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * 카테고리에서 List<옷> 을 들고 있지 않는 것으로 했던 것 같은데...?
     * 기억이 잘 안나서 주석 처리
     *
     * private List<Clothes> clothes;
     */

    // 부모 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // 외래 키 컬럼 이름
    private Category parent;

}
