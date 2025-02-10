package com.clokey.server.domain.history.domain.document;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "history")
@Mapping(mappingPath = "static/elastic-mapping.json")
@Setting(settingPath = "static/elastic-token.json")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HistoryDocument {

    @Id
    private Long historyId;  // JPA 엔티티와 동일한 id 사용

    private Long hashtagId;

    private String hashtagName;
}
