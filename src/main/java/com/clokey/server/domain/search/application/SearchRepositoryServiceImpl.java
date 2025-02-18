package com.clokey.server.domain.search.application;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.document.ClothDocument;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.history.application.HistoryClothRepositoryService;
import com.clokey.server.domain.history.application.HistoryImageRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.document.HistoryDocument;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.document.MemberDocument;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchRepositoryServiceImpl implements SearchRepositoryService {

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

    /****************************************Cloth Sync****************************************/

    // 단일 옷 데이터를 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void updateClothDataToElasticsearch(Cloth cloth) throws IOException {
        // Elasticsearch 문서 변환
        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(CLOTH_INDEX_NAME) // Elasticsearch 인덱스명
                        .id(cloth.getId().toString()) // ID 설정
                        .document(ClothDocument.builder()
                                .id(cloth.getId())
                                .name(cloth.getName())
                                .brand(cloth.getBrand())
                                .imageUrl(cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                                .wearNum(cloth.getWearNum())
                                .memberId(cloth.getMember().getId())
                                .visibility(cloth.getVisibility().toString())
                                .build())
                )));

        // Bulk 요청 실행 (단일 문서 업데이트)
        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(CLOTH_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        // Bulk 처리 결과 로그 출력
        if (bulkResponse.errors()) {
            // 오류 발생 시 로그 출력
            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 모든 옷 데이터 삭제하는 메서드
    @Override
    @Transactional
    public void deleteClothesByMemberIdFromElasticsearch(Long memberId) throws IOException {

        // Elasticsearch에서 해당 memberId를 가진 모든 Cloth 삭제
        DeleteByQueryResponse deleteResponse = elasticsearchClient.deleteByQuery(d -> d
                .index(CLOTH_INDEX_NAME)
                .query(q -> q
                        .term(t -> t.field("memberId").value(memberId)) // 특정 memberId의 모든 Cloth 삭제
                )
        );

        // 삭제 처리 결과 로그 출력
        if (deleteResponse.deleted() == 0) {
            // 오류 발생 시 삭제 실패 로그 출력
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 옷에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // 특정 옷 Elasticsearch에서 삭제하는 메서드
    @Override
    @Transactional
    public void deleteClothByIdFromElasticsearch(Long clothId) throws IOException {
        // Elasticsearch에서 해당 clothId에 해당하는 데이터 삭제
        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(CLOTH_INDEX_NAME)
                .id(clothId.toString()) // clothId에 해당하는 단일 문서 삭제
        );

        // 삭제 처리 결과 로그 출력
        if (!deleteResponse.result().equals(Result.Deleted)) {
            // 오류 발생 시 삭제 실패 로그 출력
            System.err.println("Elasticsearch에서 clothId: " + clothId + " 에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 Cloth 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void syncAllClothesDataToElasticsearch() throws IOException {
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
                                        .memberId(cloth.getMember().getId())
                                        .visibility(cloth.getVisibility().toString())
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
                // 오류 발생 시 BulkResponse를 로그로 출력
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

    /****************************************History Sync****************************************/

    // 단일 기록 데이터를 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void updateHistoryDataToElasticsearch(History history) throws IOException {
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
        String imageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                .sorted(Comparator.comparing(HistoryImage::getCreatedAt)) // 생성일 기준 정렬
                .map(HistoryImage::getImageUrl)
                .findFirst() // 첫 번째 요소 가져오기
                .orElse(null); // 비어있으면 null 반환

        // Elasticsearch 문서 변환
        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(HISTORY_INDEX_NAME)
                        .id(history.getId().toString()) // ID 설정
                        .document(HistoryDocument.builder()
                                .id(history.getId())
                                .hashtagNames(hashtagNames)
                                .categoryNames(categoryNames)
                                .imageUrl(imageUrl)
                                .memberVisibility(history.getMember().getVisibility().toString())
                                .historyVisibility(history.getVisibility().toString())
                                .build())
                )));

        // Bulk 요청 실행 (단일 문서 업데이트)
        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(HISTORY_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        // Bulk 처리 결과 로그 출력
        if (bulkResponse.errors()) {
            // 오류 발생 시 로그 출력
            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 모든 기록 데이터 삭제하는 메서드
    @Override
    @Transactional
    public void deleteHistoriesByMemberIdFromElasticsearch(Long memberId) throws IOException {

        // Elasticsearch에서 해당 memberId를 가진 모든 History 삭제
        DeleteByQueryResponse deleteResponse = elasticsearchClient.deleteByQuery(d -> d
                .index(HISTORY_INDEX_NAME)
                .query(q -> q
                        .term(t -> t.field("memberId").value(memberId)) // 특정 memberId의 모든 History 삭제
                )
        );

        // 삭제 처리 결과 로그 출력
        if (deleteResponse.deleted() == 0) {
            // 오류 발생 시 삭제 실패 로그 출력
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 기록에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // 특정 옷 Elasticsearch에서 삭제하는 메서드
    @Override
    @Transactional
    public void deleteHistoryByIdFromElasticsearch(Long historyId) throws IOException {
        // Elasticsearch에서 해당 historyId에 해당하는 데이터 삭제
        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(HISTORY_INDEX_NAME)
                .id(historyId.toString()) // historyId에 해당하는 단일 문서 삭제
        );

        // 삭제 처리 결과 로그 출력
        if (!deleteResponse.result().equals(Result.Deleted)) {
            // 오류 발생 시 삭제 실패 로그 출력
            System.err.println("Elasticsearch에서 clothId: " + historyId + " 에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 History 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void syncAllHistoriesDataToElasticsearch() throws IOException {
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
                    String imageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                            .sorted(Comparator.comparing(HistoryImage::getCreatedAt)) // 생성일 기준 정렬
                            .map(HistoryImage::getImageUrl)
                            .findFirst() // 첫 번째 요소 가져오기
                            .orElse(null); // 비어있으면 null 반환

                    return BulkOperation.of(op -> op
                            .index(IndexOperation.of(idx -> idx
                                    .index(HISTORY_INDEX_NAME) // Elasticsearch 인덱스명
                                    .id(history.getId().toString()) // ID 설정
                                    .document(HistoryDocument.builder()
                                            .id(history.getId())
                                            .hashtagNames(hashtagNames)
                                            .categoryNames(categoryNames)
                                            .imageUrl(imageUrl)
                                            .memberVisibility(history.getMember().getVisibility().toString())
                                            .historyVisibility(history.getVisibility().toString())
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
                // 오류 발생 시 BulkResponse를 로그로 출력
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

    /****************************************Member Sync****************************************/

    // 단일 유저 데이터를 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void updateMemberDataToElasticsearch(Member member) throws IOException {
        // Elasticsearch 문서 변환
        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(MEMBER_INDEX_NAME) // Elasticsearch 인덱스명
                        .id(member.getId().toString()) // ID 설정
                        .document(MemberDocument.builder()
                                .id(member.getId())
                                .nickname(member.getNickname())
                                .clokeyId(member.getClokeyId())
                                .profileUrl(member.getProfileImageUrl())
                                .build())
                )));

        // Bulk 요청 실행 (단일 문서 업데이트)
        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(MEMBER_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        // Bulk 처리 결과 로그 출력
        if (bulkResponse.errors()) {
            // 오류 발생 시 로그 출력
            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 유저 데이터 삭제하는 메서드
    @Override
    @Transactional
    public void deleteMemberByMemberIdFromElasticsearch(Long memberId) throws IOException {

        // Elasticsearch에서 해당 memberId에 해당하는 문서 삭제
        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(MEMBER_INDEX_NAME)
                .id(memberId.toString()) // memberId를 기반으로 삭제
        );

        // 삭제 처리 결과 로그 출력
        if (!deleteResponse.result().equals(Result.Deleted)) {
            // 오류 발생 시 삭제 실패 로그 출력
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 멤버에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 Member 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    @Transactional
    public void syncAllMembersDataToElasticsearch() throws IOException {
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
                // 오류 발생 시 BulkResponse를 로그로 출력
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

}
