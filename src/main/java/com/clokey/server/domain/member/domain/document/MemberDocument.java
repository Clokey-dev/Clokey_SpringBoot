package com.clokey.server.domain.member.domain.document;

import com.clokey.server.domain.member.domain.entity.Member;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "user")
@Mapping(mappingPath = "static/elastic-mapping.json")
@Setting(settingPath = "static/elastic-token.json")
public class MemberDocument {

    @Id
    private Long id;  // JPA 엔티티와 동일한 id 사용

    private String nickname;

    private String clokeyId;

    private String profileUrl;

    public static MemberDocument from(Member member) {
        return MemberDocument.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .clokeyId(member.getClokeyId())
                .profileUrl(member.getProfileImageUrl())
                .build();
    }
}
