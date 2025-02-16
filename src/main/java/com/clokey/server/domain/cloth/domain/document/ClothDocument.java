package com.clokey.server.domain.cloth.domain.document;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "cloth")
@Mapping(mappingPath = "static/elastic-mapping.json")
@Setting(settingPath = "static/elastic-token.json")
public class ClothDocument {

    @Id
    private Long id;  // JPA 엔티티와 동일한 id 사용

    private String name;

    private String brand;

    private String imageUrl;

    private int wearNum;

    private Long memberId;

    public static ClothDocument from(Cloth cloth) {
        return ClothDocument.builder()
                .id(cloth.getId())
                .name(cloth.getName())
                .brand(cloth.getBrand())
                .imageUrl(cloth.getImage().getImageUrl())
                .wearNum(cloth.getWearNum())
                .build();
    }
}
