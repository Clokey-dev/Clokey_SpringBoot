package com.clokey.server.domain.cloth.domain.entity;

import com.clokey.server.domain.cloth.exception.ClothException;
import com.clokey.server.domain.model.entity.BaseEntity;
import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;

import com.clokey.server.global.error.code.status.ErrorStatus;
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

    @Column(nullable = true, length = 1000)
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

    public void increaseWearNum() {
        this.wearNum ++;
    }

    public void decreaseWearNum() {
        if(wearNum == 0){
            throw new ClothException(ErrorStatus.CLOTH_WEAR_NUM_BELOW_ZERO);
        }
        this.wearNum--;
    }

    /**
     * Cloth 엔티티 업데이트 메서드
     */
    public void updateCloth(String name,
                            List<Season> seasons,
                            int tempUpperBound,
                            int tempLowerBound,
                            ThicknessLevel thicknessLevel,
                            Visibility visibility,
                            String clothUrl,
                            String brand,
                            Long categoryId,
                            String imageUrl) {
        if (name != null) this.name = name;
        if (seasons != null) {
            this.seasons.clear(); // 기존 계절 리스트 초기화
            this.seasons.addAll(seasons); // 새 값으로 업데이트
        }
        if (tempUpperBound != 0) this.tempUpperBound = tempUpperBound;
        if (tempLowerBound != 0) this.tempLowerBound = tempLowerBound;
        if (thicknessLevel != null) this.thicknessLevel = thicknessLevel;
        if (visibility != null) this.visibility = visibility;
        if (clothUrl != null) this.clothUrl = clothUrl;
        if (brand != null) this.brand = brand;
        if (categoryId != null) {
            this.category = Category.builder().id(categoryId).build();
        }

        // 이미지 업데이트
        if (imageUrl != null) {
            if (this.image == null) {
                // 새로운 이미지 생성
                this.image = ClothImage.builder()
                        .imageUrl(imageUrl)
                        .cloth(this)
                        .build();
            } else {
                // 기존 이미지 URL 업데이트
                this.image.updateClothImageUrl(imageUrl);
            }
        }
    }
}
