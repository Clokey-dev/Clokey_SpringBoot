package com.clokey.server.domain.search.application;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
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
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
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

    private final MemberRepositoryService memberRepositoryService;

    private static final String MEMBER_INDEX_NAME = "user";

    private static final String CLOTH_INDEX_NAME = "cloth";

    private static final String HISTORY_INDEX_NAME = "history";

    /****************************************Search Method****************************************/

    // 옷 이름과 브랜드로 검색하는 메서드
    @Override
    public ClothResponseDTO.ClothPreviewListResult searchClothesByNameOrBrand(Long requestedMemberId, String clokeyId, String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page-1, size);

        Long memberId = memberRepositoryService.findMemberByClokeyId(clokeyId).getId();
        boolean isOwner = requestedMemberId.equals(memberId); // 내 계정인지 확인

        SearchResponse<ClothDocument> response = elasticsearchClient.search(s -> s
                        .index(CLOTH_INDEX_NAME)
                        .query(q -> q.bool(b -> {
                            // 특정 멤버의 옷만 필터링
                            b.must(m -> m.term(t -> t.field("memberId").value(memberId)));

                            // 내 계정이 아니면 비공개(visibility: PRIVATE) 옷 제외
                            if (!isOwner) {
                                b.mustNot(m -> m.term(t -> t.field("visibility.keyword").value("PRIVATE")));
                            }

                            // 이름 또는 브랜드에서 부분 검색 (OR 조건 적용)
                            b.must(m -> m.bool(bb -> bb
                                    .should(ms -> ms.match(mq -> mq
                                            .field("name")
                                            .query(keyword)
                                            .fuzziness("AUTO")
                                    ))
                                    .should(ms -> ms.matchBoolPrefix(mq -> mq
                                            .field("name")
                                            .query(keyword)
                                            .fuzziness("AUTO")
                                    ))
                                    .should(ms -> ms.matchPhrasePrefix(mq -> mq
                                            .field("name")
                                            .query(keyword)
                                    ))
                                    .should(ms -> ms.match(mq -> mq
                                            .field("brand")
                                            .query(keyword)
                                            .fuzziness("AUTO")
                                    ))
                                    .should(ms -> ms.matchPhrasePrefix(mq -> mq
                                            .field("brand")
                                            .query(keyword)
                                    ))
                            ));
                            return b;
                        }))
                        .from((int) pageable.getOffset())
                        .size(pageable.getPageSize()),
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

    // 기록의 해쉬태그와 카테고리로 검색하는 메서드
    @Override
    public HistoryResponseDTO.HistoryPreviewListResult searchHistoriesByHashtagAndCategory(String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page - 1, size);

        SearchResponse<HistoryDocument> response = elasticsearchClient.search(s -> s
                        .index(HISTORY_INDEX_NAME)
                        .query(q -> q.bool(b -> {
                            // 비공개 계정의 기록 제외
                            b.mustNot(m -> m.term(t -> t.field("memberVisibility.keyword").value("PRIVATE")));

                            // 비공개 기록 제외
                            b.mustNot(m -> m.term(t -> t.field("historyVisibility.keyword").value("PRIVATE")));

                            // 검색시 사진이 없으면 제외; 기록이 공개인데, 옷이 비공개여서 띄워줄 사진이 없는 경우
                            b.must(m -> m.exists(t -> t.field("imageUrl")));
                            // 빈 값 또는 "null"이면 제외
                            b.mustNot(m -> m.terms(t -> t.field("imageUrl.keyword")
                                    .terms(TermsQueryField.of(f -> f.value(List.of(FieldValue.of(""), FieldValue.of("null")))))));

                            // 해시태그 및 카테고리 검색
                            b.should(m -> m.multiMatch(t -> t
                                    .query(keyword)
                                    .fields("hashtagNames", "categoryNames")
                                    .fuzziness("AUTO")
                            ));

                            // 해시태그 검색
                            b.should(m -> m.term(t -> t
                                    .field("hashtagNames.keyword")
                                    .value(keyword)
                            ));
                            b.should(m -> m.matchBoolPrefix(t -> t
                                    .field("hashtagNames")
                                    .query(keyword)
                            ));
                            b.should(m -> m.matchPhrasePrefix(t -> t
                                    .field("hashtagNames")
                                    .query(keyword)
                            ));
                            b.should(m -> m.wildcard(t -> t
                                    .field("hashtagNames.keyword")
                                    .value("*" + keyword + "*")
                            ));
                            // 카테고리 검색
                            b.must(m -> m.matchBoolPrefix(t -> t
                                    .field("categoryNames")
                                    .query(keyword)
                            ));
                            b.must(m -> m.matchPhrasePrefix(t -> t
                                    .field("categoryNames")
                                    .query(keyword)
                            ));
                            return b;
                        }))
                        .from((int) pageable.getOffset())
                        .size(pageable.getPageSize()),
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

    // 유저의 Clokey Id 또는 닉네임으로 검색하는 메서드
    @Override
    public MemberDTO.ProfilePreviewListRP searchMembersByClokeyIdOrNickname(String keyword, int page, int size) throws IOException {

        Pageable pageable = PageRequest.of(page - 1, size);

        SearchResponse<MemberDocument> response = elasticsearchClient.search(s -> s
                        .index(MEMBER_INDEX_NAME)
                        .query(q -> q.bool(b -> b
                                .should(m -> m.multiMatch(t -> t
                                        .query(keyword)
                                        .fields("nickname", "clokeyId")
                                        .fuzziness("AUTO")
                                ))
                                .should(m -> m.multiMatch(t -> t
                                        .query(keyword)
                                        .fields("nickname")
                                        .type(TextQueryType.BoolPrefix)
                                        .fuzziness("AUTO")
                                ))
                                .should(m -> m.wildcard(t -> t
                                        .field("nickname")
                                        .value("*" + keyword + "*")
                                ))
                                .should(m -> m.matchPhrasePrefix(t -> t
                                        .field("clokeyId")
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
}
