package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.document.MemberDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MemberSearchRepository extends ElasticsearchRepository<MemberDocument, String> {

    List<MemberDocument> findByClokeyIdAndNickname(String clokeyId, String nickname);
}
