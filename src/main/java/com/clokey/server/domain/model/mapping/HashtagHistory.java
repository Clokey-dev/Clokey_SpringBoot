package com.clokey.server.domain.model.mapping;

import com.clokey.server.domain.model.BaseEntity;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.Hashtag;
import com.clokey.server.domain.model.History;
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
