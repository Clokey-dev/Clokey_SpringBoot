package com.clokey.server.domain.cloth.domain.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "cloth")
@Mapping(mappingPath = "static/elastic-mapping.json")
@Setting(settingPath = "static/elastic-token.json")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClothDocument {

    @Id
    private Long id;  // JPA 엔티티와 동일한 id 사용

    private String name;

    private String brand;
}
