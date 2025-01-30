package com.clokey.server.domain.term.domain.entity;

import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Term extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private Boolean optional;
}
