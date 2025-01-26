package com.clokey.server.domain.model.entity;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import com.clokey.server.domain.model.entity.enums.Visibility;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
public class Cloth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int wearNum;

    @ElementCollection(fetch = FetchType.LAZY) // 다중 값을 위한 설정
    @CollectionTable(
            name = "cloth_season", // 매핑될 계절 테이블 이름
            joinColumns = @JoinColumn(name = "cloth_id") // 부모 테이블과의 조인 컬럼
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "season", nullable = false) // 해당 season을 cloth_seasons 테이블에만 저장
    private List<Season> seasons = new ArrayList<>();

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempUpperBound;

    @Min(-20)
    @Max(40)
    @Column(nullable = false)
    private int tempLowerBound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ThicknessLevel thicknessLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Column(nullable = true)
    private String clothUrl;

    @Column(nullable = true)
    private String brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToOne(mappedBy = "cloth", cascade = CascadeType.ALL, orphanRemoval = true)
    private ClothImage image;

    public Long getMemberId() {
        return this.member != null ? this.member.getId() : null;
    }

    /**
     * Cloth 엔티티 업데이트 메서드
     */
    public void updateCloth(ClothRequestDTO.ClothCreateOrUpdateRequest request, String imageUrl) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getSeasons() != null) this.seasons = request.getSeasons();
        if (request.getTempUpperBound() != 0) this.tempUpperBound = request.getTempUpperBound();
        if (request.getTempLowerBound() != 0) this.tempLowerBound = request.getTempLowerBound();
        if (request.getThicknessLevel() != null) this.thicknessLevel = request.getThicknessLevel();
        if (request.getVisibility() != null) this.visibility = request.getVisibility();
        if (request.getClothUrl() != null) this.clothUrl = request.getClothUrl();
        if (request.getBrand() != null) this.brand = request.getBrand();
        if (request.getCategoryId() != null) {
            this.category = Category.builder().id(request.getCategoryId()).build();
        }

        // 이미지 업데이트
        if (imageUrl != null) {
            this.image = ClothImage.builder()
                    .imageUrl(imageUrl)
                    .cloth(this)
                    .build();
        }
    }
}
