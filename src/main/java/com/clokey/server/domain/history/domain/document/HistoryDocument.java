package com.clokey.server.domain.history.domain.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "history")
@Mapping(mappingPath = "static/elastic-mapping-history.json")
@Setting(settingPath = "static/elastic-setting-history.json")
public class HistoryDocument {

    @Id
    private Long id;  // JPA 엔티티와 동일한 id 사용

    private List<String> hashtagNames;

    private List<String> categoryNames;

    private String imageUrl;

    private String memberVisibility;

    private String historyVisibility;
}
