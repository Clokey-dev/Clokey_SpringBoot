package com.clokey.server.domain.cloth.domain.document;

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
@Setting(settingPath = "static/elastic-setting.json")
public class ClothDocument {

    @Id
    private Long id;  // JPA 엔티티와 동일한 id 사용

    private String name;

    private String brand;

    private String imageUrl;

    private int wearNum;

    private Long memberId;

    private String visibility;
}
