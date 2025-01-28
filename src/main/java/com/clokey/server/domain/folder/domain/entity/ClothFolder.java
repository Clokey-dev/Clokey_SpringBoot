package com.clokey.server.domain.folder.domain.entity;

import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClothFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cloth_id",nullable = false)
    private Cloth cloth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id",nullable = false)
    private Folder folder;

    public ClothFolder(Cloth cloth, Folder folder) {
        this.cloth = cloth;
        this.folder = folder;
    }
}
