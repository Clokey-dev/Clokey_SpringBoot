package com.clokey.server.domain.search.application;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.domain.document.ClothDocument;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {



    private final ElasticsearchClient elasticsearchClient;

    private final ClothRepositoryService clothRepositoryService;
    private static final String CLOTH_INDEX_NAME = "cloth";

    // JPA에서 모든 Cloth 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Transactional
    public void syncClothesDataToElasticsearch() throws IOException {
        // JPA에서 모든 데이터 조회
        List<Cloth> clothList = clothRepositoryService.findAll();

        // Cloth 데이터를 Elasticsearch 문서로 변환
        List<BulkOperation> bulkOperations = clothList.stream()
                .map(cloth -> BulkOperation.of(op -> op
                        .index(IndexOperation.of(idx -> idx
                                .index(CLOTH_INDEX_NAME) // Elasticsearch 인덱스명
                                .id(cloth.getId().toString()) // ID 설정
                                .document(ClothDocument.builder()
                                        .id(cloth.getId())
                                        .name(cloth.getName())
                                        .brand(cloth.getBrand())
                                        .imageUrl(cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                                        .wearNum(cloth.getWearNum())
                                        .build())
                        ))))
                .collect(Collectors.toList());

        // Bulk 요청 실행
        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(CLOTH_INDEX_NAME)
                    .operations(bulkOperations)
            );

            // Bulk 처리 결과 로그 출력
            if (bulkResponse.errors()) {
                throw new RuntimeException("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());
            }
        }
    }

    // 옷 이름과 브랜드로 검색하는 메서드
    public ClothResponseDTO.ClothPreviewListResult searchClothesByNameOrBrand(String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page-1, size);

        SearchResponse<ClothDocument> response = elasticsearchClient.search(s -> s
                        .index(CLOTH_INDEX_NAME)
                        .query(q -> q.bool(b -> b
                                .should(m -> m.multiMatch(t -> t
                                        .query(keyword)
                                        .fields("name", "brand")
                                        .fuzziness("AUTO")
                                ))
                                .should(m -> m.matchBoolPrefix(t -> t
                                        .field("name")
                                        .query(keyword)
                                ))
                        )),
                ClothDocument.class
        );

        List<ClothDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        Page<ClothDocument> clothDocuments = new PageImpl<>(results, pageable, response.hits().total().value());

        // Cloth Document -> ClothPreview DTO 변환
        List<ClothResponseDTO.ClothPreview> clothPreviews = ClothConverter.toClothPreviewList(clothDocuments);

        // 페이징 정보를 담아 DTO 반환
        return ClothConverter.toClothPreviewListResult(clothDocuments, clothPreviews);
    }
}
