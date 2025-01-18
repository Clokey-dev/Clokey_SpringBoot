package com.clokey.server.domain.model.entity.mapping;

import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.Hashtag;
import com.clokey.server.domain.model.entity.History;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HashtagHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id",nullable = false)
    private History history;
}
