package com.clokey.server.domain.cloth.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.category.exception.CategoryException;
import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.folder.application.ClothFolderRepositoryService;
import com.clokey.server.domain.history.application.HistoryClothRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.SummaryFrequency;
import com.clokey.server.domain.search.application.SearchRepositoryService;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.infra.s3.S3ImageService;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepositoryService clothRepositoryService;
    private final ClothImageRepositoryService clothImageRepositoryService;
    private final ClothFolderRepositoryService clothFolderRepositoryService;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final S3ImageService s3ImageService;
    private final SearchRepositoryService searchRepositoryService;
    private final MemberRepositoryService memberRepositoryService;

    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId) {

        Cloth cloth = clothRepositoryService.findById(clothId);

        return ClothConverter.toClothPopupViewResult(cloth);
    }

    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId){

        Cloth cloth = clothRepositoryService.findById(clothId);

        return ClothConverter.toClothEditViewResult(cloth);
    }

    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId){

        Cloth cloth = clothRepositoryService.findById(clothId);

        return ClothConverter.toClothDetailViewResult(cloth);
    }

    // 옷장의 옷의 PreView 조회 후 옷장 조회 DTO로 변환해서 반환
    @Transactional(readOnly = true)
    public ClothResponseDTO.ClothPreviewListResult readClothPreviewInfoListByClokeyId(
            String ownerClokeyId, Long requesterId, Long categoryId, Season season, ClothSort sort, int page, int size) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Cloth> clothes = clothRepositoryService.findByClosetFilters(ownerClokeyId, requesterId, categoryId, season, sort, pageable);

        List<ClothResponseDTO.ClothPreview> clothPreviews = ClothConverter.toClothPreviewList(clothes);

        return ClothConverter.toClothPreviewListResult(clothes, clothPreviews);
    }

    // 지난 7일간 착용횟수를 통해 카테고리와 카테고리에 해당하는 옷의 PreView 조회 후 스마트 요약 DTO로 변환해서 반환
    @Transactional(readOnly = true)
    public ClothResponseDTO.SmartSummaryClothPreviewListResult readSmartSummary(Long memberId) {

        String nickname = memberRepositoryService.findMemberById(memberId).getNickname();

        List<History> histories = historyRepositoryService.findHistoriesByMemberWithinWeek(memberId);

        List<Cloth> clothes = histories.stream()
                .flatMap(history -> historyClothRepositoryService.findAllClothByHistoryId(history.getId()).stream())
                .collect(Collectors.toList());

        Map<Category, Long> categoryCountMap = clothes.stream()
                .collect(Collectors.groupingBy(Cloth::getCategory, Collectors.counting()));

        List<Map.Entry<Category, Long>> filteredEntries = categoryCountMap.entrySet().stream()
                .filter(entry -> entry.getKey().getParent() != null)
                .collect(Collectors.toList());

        if (filteredEntries.isEmpty()) {
            throw new CategoryException(ErrorStatus.CATEGORY_NOT_FOUND_IN_SUMMARY);
        }

        Map.Entry<Category, Long> frequentEntry = filteredEntries.stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new CategoryException(ErrorStatus.CATEGORY_NOT_FOUND_IN_SUMMARY));
        Map.Entry<Category, Long> infrequentEntry = filteredEntries.stream()
                .min(Map.Entry.comparingByValue())
                .orElseThrow(() -> new CategoryException(ErrorStatus.CATEGORY_NOT_FOUND_IN_SUMMARY));

        List<Cloth> frequentClothes = clothRepositoryService.findBySmartSummaryFilters(
                SummaryFrequency.FREQUENT, memberId, frequentEntry.getKey().getId());
        List<ClothResponseDTO.ClothPreview> frequentClothPreviews = ClothConverter.toClothPreviewList(frequentClothes);

        List<Cloth> infrequentClothes = clothRepositoryService.findBySmartSummaryFilters(
                SummaryFrequency.INFREQUENT, memberId, infrequentEntry.getKey().getId());
        List<ClothResponseDTO.ClothPreview> infrequentClothPreviews = ClothConverter.toClothPreviewList(infrequentClothes);

        return ClothConverter.toSummaryClothPreviewListResult(
                nickname,
                frequentEntry.getKey(),           // 자주 입은 카테고리 객체
                infrequentEntry.getKey(),         // 덜 입은 카테고리 객체
                frequentEntry.getValue(),         // 자주 입은 카테고리 착용 횟수
                infrequentEntry.getValue(),       // 덜 입은 카테고리 착용 횟수
                frequentClothPreviews,            // 자주 입은 카테고리의 Cloth PreView 목록
                infrequentClothPreviews           // 덜 입은 카테고리의 Cloth PreView 목록
        );
    }

    @Transactional
    public ClothResponseDTO.ClothCreateResult createCloth(Long memberId,
                                                          ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                                          MultipartFile imageFile) {

        Cloth cloth = clothRepositoryService.save(ClothConverter.toCloth(memberId, request));

        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        ClothImage clothImage = ClothImage.builder()
                .imageUrl(imageUrl)
                .cloth(cloth)
                .build();

        clothImageRepositoryService.save(clothImage);

        try {
            searchRepositoryService.updateClothDataToElasticsearch(cloth);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }

        return ClothConverter.toClothCreateResult(cloth);
    }

    @Transactional
    public void updateClothById(Long clothId,
                                ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                MultipartFile imageFile){

        Cloth existingCloth = clothRepositoryService.findById(clothId);

        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        existingCloth.updateCloth(
                request.getName(),
                request.getSeasons(),
                request.getTempUpperBound(),
                request.getTempLowerBound(),
                request.getThicknessLevel(),
                request.getVisibility(),
                request.getClothUrl(),
                request.getBrand(),
                request.getCategoryId(),
                imageUrl
        );

        // ES 동기화
        try {
            searchRepositoryService.updateClothDataToElasticsearch(existingCloth);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }
    }

    @Transactional
    public void deleteClothById(Long clothId){
        historyClothRepositoryService.deleteAllByClothId(clothId);
        clothFolderRepositoryService.deleteAllByClothId(clothId);
        clothImageRepositoryService.deleteByClothId(clothId);
        clothRepositoryService.deleteById(clothId);

        try {
            searchRepositoryService.deleteClothByIdFromElasticsearch(clothId);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }
}
