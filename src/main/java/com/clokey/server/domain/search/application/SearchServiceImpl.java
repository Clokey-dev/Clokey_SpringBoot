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
import com.clokey.server.domain.history.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.history.application.HistoryClothRepositoryService;
import com.clokey.server.domain.history.application.HistoryImageRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.domain.document.HistoryDocument;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.converter.MemberDocumentConverter;
import com.clokey.server.domain.member.domain.document.MemberDocument;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;

    private final ClothRepositoryService clothRepositoryService;
    private static final String CLOTH_INDEX_NAME = "cloth";

    private final MemberRepositoryService memberRepositoryService;
    private static final String MEMBER_INDEX_NAME = "user";

    private final HistoryRepositoryService historyRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    private final HistoryImageRepositoryService historyImageRepositoryService;
    private static final String HISTORY_INDEX_NAME = "history";

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

    // JPA에서 모든 Member 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Transactional
    public void syncMembersDataToElasticsearch() throws IOException {
        // JPA에서 모든 Member 데이터 조회
        List<Member> memberList = memberRepositoryService.findAll();

        // Member 데이터를 Elasticsearch 문서로 변환
        List<BulkOperation> bulkOperations = memberList.stream()
                .map(member -> BulkOperation.of(op -> op
                        .index(IndexOperation.of(idx -> idx
                                .index(MEMBER_INDEX_NAME) // Elasticsearch 인덱스명
                                .id(member.getId().toString()) // ID 설정
                                .document(MemberDocument.builder()
                                        .id(member.getId())
                                        .nickname(member.getNickname())
                                        .clokeyId(member.getClokeyId())
                                        .profileUrl(member.getProfileImageUrl())
                                        .build())
                        ))))
                .collect(Collectors.toList());

        // Bulk 요청 실행
        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(MEMBER_INDEX_NAME)
                    .operations(bulkOperations)
            );

            // Bulk 처리 결과 로그 출력
            if (bulkResponse.errors()) {
                throw new RuntimeException("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());
            }
        }
    }

    // 유저의 Clokey Id 또는 닉네임으로 검색하는 메서드
    public MemberDTO.ProfilePreviewListRP searchMembersByClokeyIdOrNickname(String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page - 1, size);

        SearchResponse<MemberDocument> response = elasticsearchClient.search(s -> s
                        .index(MEMBER_INDEX_NAME)
                        .query(q -> q.bool(b -> b
                                .should(m -> m.multiMatch(t -> t
                                        .query(keyword)
                                        .fields("clokeyId", "nickname")
                                        .fuzziness("AUTO")
                                ))
                                .should(m -> m.matchBoolPrefix(t -> t
                                        .field("clokeyId")
                                        .query(keyword)
                                ))
                                .should(m -> m.matchBoolPrefix(t -> t
                                        .field("nickname")
                                        .query(keyword)
                                ))
                        )),
                MemberDocument.class
        );

        List<MemberDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        Page<MemberDocument> memberDocuments = new PageImpl<>(results, pageable, response.hits().total().value());

        // Member Document -> ProfilePreview DTO 변환
        List<MemberDTO.ProfilePreview> memberPreviews = MemberDocumentConverter.toProfilePreviewList(memberDocuments);

        // 페이징 정보를 담아 DTO 반환
        return MemberDocumentConverter.toProfilePreviewListRP(memberDocuments, memberPreviews);
    }

    // JPA에서 모든 History 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Transactional
    public void syncHistoriesDataToElasticsearch() throws IOException {
        // JPA에서 모든 History 데이터 조회
        List<History> historyList = historyRepositoryService.findAll();

        // History 데이터를 Elasticsearch 문서로 변환
        List<BulkOperation> bulkOperations = historyList.stream()
                .map(history -> {

                    // Hashtag 가져오기
                    List<String> hashtagNames = hashtagHistoryRepositoryService.findHashtagNamesByHistoryId(history.getId());

                    // 태그된 cloth 가져오기
                    List<Cloth> clothes = historyClothRepositoryService.findAllClothByHistoryId(history.getId());

                    // Category 가져오기 (태그된 cloth 기반)
                    List<String> categoryNames = clothes.stream()
                            .map(cloth -> cloth.getCategory().getName()) // Cloth에서 직접 Category 가져오기
                            .distinct() // 중복 제거
                            .collect(Collectors.toList());

                    // Image URL 가져오기
                    // HistoryImage에서 가장 먼저 생성된 이미지 가져오기
                    List<String> imageUrls = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                            .sorted(Comparator.comparing(HistoryImage::getCreatedAt)) // 생성일 기준 정렬
                            .map(HistoryImage::getImageUrl)
                            .toList();

                    // 첫 번째 이미지 선택 (없다면 대체 이미지 가져오기)
                    String imageUrl = imageUrls.isEmpty()
                            ? historyImageRepositoryService.findFirstImagesByHistoryIds(List.of(history.getId())).get(history.getId()) // HistoryImage가 없다면 대체 이미지 사용
                            : imageUrls.get(0); // 존재하면 첫 번째 이미지 사용

                    return BulkOperation.of(op -> op
                            .index(IndexOperation.of(idx -> idx
                                    .index(HISTORY_INDEX_NAME) // Elasticsearch 인덱스명
                                    .id(history.getId().toString()) // ID 설정
                                    .document(HistoryDocument.builder()
                                            .id(history.getId())
                                            .hashtagNames(hashtagNames)
                                            .categoryNames(categoryNames)
                                            .imageUrl(imageUrl)
                                            .build())
                            )));
                })
                .collect(Collectors.toList());

        // Bulk 요청 실행
        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(HISTORY_INDEX_NAME)
                    .operations(bulkOperations)
            );

            // Bulk 처리 결과 로그 출력
            if (bulkResponse.errors()) {
                throw new RuntimeException("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());
            }
        }
    }

    // 기록의 해쉬태그와 카테고리로 검색하는 메서드
    public HistoryResponseDTO.HistoryPreviewListResult searchHistoriesByHashtagAndCategory(String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page - 1, size);

        SearchResponse<HistoryDocument> response = elasticsearchClient.search(s -> s
                        .index(HISTORY_INDEX_NAME)
                        .query(q -> q.bool(b -> b
                                .should(m -> m.multiMatch(t -> t
                                        .query(keyword)
                                        .fields("hashtagNames", "categoryNames")
                                        .fuzziness("AUTO")
                                ))
                                .should(m -> m.matchBoolPrefix(t -> t
                                        .field("hashtagNames")
                                        .query(keyword)
                                ))
                                .should(m -> m.matchBoolPrefix(t -> t
                                        .field("categoryNames")
                                        .query(keyword)
                                ))
                        )),
                HistoryDocument.class
        );

        List<HistoryDocument> results = response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());

        Page<HistoryDocument> historyDocuments = new PageImpl<>(results, pageable, response.hits().total().value());

        // History Document -> HistoryPreview DTO 변환
        List<HistoryResponseDTO.HistoryPreview> historyPreviews = HistoryConverter.toHistoryPreviewList(historyDocuments);

        // 페이징 정보를 담아 DTO 반환
        return HistoryConverter.toHistoryPreviewListResult(historyDocuments, historyPreviews);
    }
}
