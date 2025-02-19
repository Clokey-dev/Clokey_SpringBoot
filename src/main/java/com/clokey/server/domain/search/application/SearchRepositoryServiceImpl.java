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
    public void updateClothDataToElasticsearch(Cloth cloth) throws IOException {

        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(CLOTH_INDEX_NAME)
                        .id(cloth.getId().toString())
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

        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(CLOTH_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        if (bulkResponse.errors()) {

            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 모든 옷 데이터 삭제하는 메서드
    @Override
    public void deleteClothesByMemberIdFromElasticsearch(Long memberId) throws IOException {

        DeleteByQueryResponse deleteResponse = elasticsearchClient.deleteByQuery(d -> d
                .index(CLOTH_INDEX_NAME)
                .query(q -> q
                        .term(t -> t.field("memberId").value(memberId))
                )
        );

        if (deleteResponse.deleted() == 0) {
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 옷에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // 특정 옷 Elasticsearch에서 삭제하는 메서드
    @Override
    public void deleteClothByIdFromElasticsearch(Long clothId) throws IOException {

        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(CLOTH_INDEX_NAME)
                .id(clothId.toString())
        );

        if (!deleteResponse.result().equals(Result.Deleted)) {
            System.err.println("Elasticsearch에서 clothId: " + clothId + " 에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 Cloth 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    public void syncAllClothesDataToElasticsearch() throws IOException {
        List<Cloth> clothList = clothRepositoryService.findAll();

        List<BulkOperation> bulkOperations = clothList.stream()
                .map(cloth -> BulkOperation.of(op -> op
                        .index(IndexOperation.of(idx -> idx
                                .index(CLOTH_INDEX_NAME)
                                .id(cloth.getId().toString())
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

        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(CLOTH_INDEX_NAME)
                    .operations(bulkOperations)
            );

            if (bulkResponse.errors()) {
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

    /****************************************History Sync****************************************/

    // 단일 기록 데이터를 Elasticsearch로 저장하는 메서드
    @Override
    public void updateHistoryDataToElasticsearch(History history) throws IOException {

        List<String> hashtagNames = hashtagHistoryRepositoryService.findHashtagNamesByHistoryId(history.getId());

        List<Cloth> clothes = historyClothRepositoryService.findAllClothByHistoryId(history.getId());

        List<String> categoryNames = clothes.stream()
                .map(cloth -> cloth.getCategory().getName())
                .distinct()
                .collect(Collectors.toList());

        String imageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                .map(HistoryImage::getImageUrl)
                .findFirst()
                .orElse(null);

        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(HISTORY_INDEX_NAME)
                        .id(history.getId().toString())
                        .document(HistoryDocument.builder()
                                .id(history.getId())
                                .hashtagNames(hashtagNames)
                                .categoryNames(categoryNames)
                                .imageUrl(imageUrl)
                                .memberVisibility(history.getMember().getVisibility().toString())
                                .historyVisibility(history.getVisibility().toString())
                                .build())
                )));

        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(HISTORY_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        if (bulkResponse.errors()) {
            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 모든 기록 데이터 삭제하는 메서드
    @Override
    public void deleteHistoriesByMemberIdFromElasticsearch(Long memberId) throws IOException {

        DeleteByQueryResponse deleteResponse = elasticsearchClient.deleteByQuery(d -> d
                .index(HISTORY_INDEX_NAME)
                .query(q -> q
                        .term(t -> t.field("memberId").value(memberId))
                )
        );

        if (deleteResponse.deleted() == 0) {
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 기록에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // 특정 옷 Elasticsearch에서 삭제하는 메서드
    @Override
    public void deleteHistoryByIdFromElasticsearch(Long historyId) throws IOException {

        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(HISTORY_INDEX_NAME)
                .id(historyId.toString())
        );

        if (!deleteResponse.result().equals(Result.Deleted)) {
            System.err.println("Elasticsearch에서 clothId: " + historyId + " 에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 History 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    public void syncAllHistoriesDataToElasticsearch() throws IOException {
        List<History> historyList = historyRepositoryService.findAll();

        List<BulkOperation> bulkOperations = historyList.stream()
                .map(history -> {

                    List<String> hashtagNames = hashtagHistoryRepositoryService.findHashtagNamesByHistoryId(history.getId());

                    List<Cloth> clothes = historyClothRepositoryService.findAllClothByHistoryId(history.getId());

                    List<String> categoryNames = clothes.stream()
                            .map(cloth -> cloth.getCategory().getName())
                            .distinct()
                            .collect(Collectors.toList());

                    String imageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                            .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                            .map(HistoryImage::getImageUrl)
                            .findFirst()
                            .orElse(null);

                    return BulkOperation.of(op -> op
                            .index(IndexOperation.of(idx -> idx
                                    .index(HISTORY_INDEX_NAME)
                                    .id(history.getId().toString())
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

        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(HISTORY_INDEX_NAME)
                    .operations(bulkOperations)
            );

            if (bulkResponse.errors()) {
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

    /****************************************Member Sync****************************************/

    // 단일 유저 데이터를 Elasticsearch로 저장하는 메서드
    @Override
    public void updateMemberDataToElasticsearch(Member member) throws IOException {
        BulkOperation bulkOperation = BulkOperation.of(op -> op
                .index(IndexOperation.of(idx -> idx
                        .index(MEMBER_INDEX_NAME)
                        .id(member.getId().toString())
                        .document(MemberDocument.builder()
                                .id(member.getId())
                                .nickname(member.getNickname())
                                .clokeyId(member.getClokeyId())
                                .profileUrl(member.getProfileImageUrl())
                                .build())
                )));

        BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                .index(MEMBER_INDEX_NAME)
                .operations(List.of(bulkOperation))
        );

        if (bulkResponse.errors()) {
            System.err.println("Elasticsearch 단일 데이터 업데이트 중 오류 발생: " + bulkResponse.toString());

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    // 특정 memberId를 가진 멤버의 Elasticsearch의 유저 데이터 삭제하는 메서드
    @Override
    public void deleteMemberByMemberIdFromElasticsearch(Long memberId) throws IOException {

        DeleteResponse deleteResponse = elasticsearchClient.delete(d -> d
                .index(MEMBER_INDEX_NAME)
                .id(memberId.toString())
        );

        if (!deleteResponse.result().equals(Result.Deleted)) {
            System.err.println("Elasticsearch에서 clothId: " + memberId + "을 memberId로 가지는 멤버에 해당하는 데이터를 찾을 수 없습니다.");

            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    // JPA에서 모든 Member 데이터 가져와서 Elasticsearch로 저장하는 메서드
    @Override
    public void syncAllMembersDataToElasticsearch() throws IOException {
        List<Member> memberList = memberRepositoryService.findAll();

        List<BulkOperation> bulkOperations = memberList.stream()
                .map(member -> BulkOperation.of(op -> op
                        .index(IndexOperation.of(idx -> idx
                                .index(MEMBER_INDEX_NAME)
                                .id(member.getId().toString())
                                .document(MemberDocument.builder()
                                        .id(member.getId())
                                        .nickname(member.getNickname())
                                        .clokeyId(member.getClokeyId())
                                        .profileUrl(member.getProfileImageUrl())
                                        .build())
                        ))))
                .collect(Collectors.toList());

        if (!bulkOperations.isEmpty()) {
            BulkResponse bulkResponse = elasticsearchClient.bulk(b -> b
                    .index(MEMBER_INDEX_NAME)
                    .operations(bulkOperations)
            );

            if (bulkResponse.errors()) {
                System.err.println("Elasticsearch 동기화 중 오류 발생: " + bulkResponse.toString());

                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }
        }
    }

}
