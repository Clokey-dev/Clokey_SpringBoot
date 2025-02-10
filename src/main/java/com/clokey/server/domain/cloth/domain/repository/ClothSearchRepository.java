package com.clokey.server.domain.cloth.domain.repository;

import com.clokey.server.domain.cloth.domain.document.ClothDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ClothSearchRepository extends ElasticsearchRepository<ClothDocument, String> {
    List<ClothDocument> findByNameAndBrand(String name, String brand);
}
