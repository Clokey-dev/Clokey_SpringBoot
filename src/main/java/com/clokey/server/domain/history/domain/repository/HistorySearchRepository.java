package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.document.HistoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface HistorySearchRepository extends ElasticsearchRepository<HistoryDocument, String> {

    List<HistoryDocument> findByHashtagNameAndCategoryName(String hashtagName, String categoryName);
}