package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.category.application.CategoryRepositoryService;
import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.category.exception.CategoryException;
import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.folder.application.ClothFolderRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.application.HistoryClothRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.SummaryFrequency;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.clokey.server.global.infra.s3.S3ImageService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepositoryService clothRepositoryService;
    private final ClothImageRepositoryService clothImageRepositoryService;
    private final ClothFolderRepositoryService clothFolderRepositoryService;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final S3ImageService s3ImageService;

    public ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId) {

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothPopupViewResult(cloth);
    }

    public ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId){

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothEditViewResult(cloth);
    }

    public ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId){

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothDetailViewResult(cloth);
    }

    // 옷장의 옷의 PreView 조회 후 옷장 조회 DTO로 변환해서 반환
    public ClothResponseDTO.CategoryClothPreviewListResult readClothPreviewInfoListByClokeyId(
            String ownerClokeyId, Long requesterId, Long categoryId, Season season, ClothSort sort, int page, int size) {

        Pageable pageable = PageRequest.of(page-1, size);
        Page<Cloth> clothes = clothRepositoryService.findByClosetFilters(ownerClokeyId, requesterId, categoryId, season, sort, pageable);

        // Cloth -> ClothPreview 변환
        List<ClothResponseDTO.ClothPreview> clothPreviews = ClothConverter.toClothPreviewList(clothes);

        // 페이징 정보를 담아 DTO 반환
        return ClothConverter.toClosetClothPreviewListResult(clothes, clothPreviews);
    }

    // 지난 7일간 평균착용횟수를 통해 카테고리와 카테고리에 해당하는 옷의 PreView 조회 후 스마트 요약 DTO로 변환해서 반환
    public ClothResponseDTO.SmartSummaryClothPreviewListResult readSmartSummaryByFrequencyType(SummaryFrequency frequencyType, Long memberId) {
        List<History> histories = historyRepositoryService.findHistoriesByMemberWithinWeek(memberId);

        // 각 History ID에 연결된 모든 Cloth 조회
        List<Cloth> clothes = histories.stream()
                .flatMap(history -> historyClothRepositoryService.findAllClothByHistoryId(history.getId()).stream())
                .toList();

        // 카테고리별 개수 집계
        Map<Category, Long> categoryCountMap = clothes.stream()
                .collect(Collectors.groupingBy(Cloth::getCategory, Collectors.counting()));

        // 카테고리 및 평균 착용 횟수 추출
        Map.Entry<Category, Long> categoryEntry = getCategoryEntry(frequencyType, categoryCountMap);
        Category category = categoryEntry.getKey();
        Long count = categoryEntry.getValue();
        Double averageUsage = calculateAverageUsage(count);

        // 카테고리별 옷 목록 조회
        List<Cloth> filteredClothes = clothRepositoryService.findBySmartSummaryFilters(frequencyType, memberId, category.getId());
        List<ClothResponseDTO.ClothPreview> clothPreviews = ClothConverter.toClothPreviewList(filteredClothes);

        return ClothConverter.toSummaryClothPreviewListResult(frequencyType, category, averageUsage, clothPreviews);
    }

    // 카테고리와 착용 횟수 구하기
    private Map.Entry<Category, Long> getCategoryEntry(SummaryFrequency frequencyType, Map<Category, Long> categoryCountMap) {
        if (frequencyType == SummaryFrequency.FREQUENT) {
            return categoryCountMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue()) // 가장 많이 입은 카테고리
                    .orElseThrow(() -> new CategoryException(ErrorStatus.CATEGORY_NOT_FOUND_IN_SUMMARY));
        } else { // INFREQUENT
            return categoryCountMap.entrySet().stream()
                    .min(Map.Entry.comparingByValue()) // 가장 적게 입은 카테고리
                    .orElseThrow(() -> new CategoryException(ErrorStatus.CATEGORY_NOT_FOUND_IN_SUMMARY));
        }
    }

    // 평균 착용 횟수 계산
    private Double calculateAverageUsage(Long count) {
        return (double) count / 7; // 7일로 나누어 평균 계산
    }

    @Transactional
    public ClothResponseDTO.ClothCreateResult createCloth(Long categoryId,
                                                          Long memberId,
                                                          ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                                          MultipartFile imageFile) {

        // Cloth 엔티티 생성 후 요청 정보 반환해서 저장
        Cloth cloth = clothRepositoryService.save(ClothConverter.toCloth(categoryId,memberId,request));

        // 이미지 업로드 후 URL 반환
        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        // ClothImage 엔티티 생성 & URL 저장
        ClothImage clothImage = ClothImage.builder()
                .imageUrl(imageUrl)
                .cloth(cloth)
                .build();

        // ClothImage 저장
        clothImageRepositoryService.save(clothImage);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothCreateResult(cloth);
    }


    @Transactional
    public void updateClothById(Long clothId,
                                Long categoryId,
                                ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                MultipartFile imageFile){

        // 기존 Cloth 조회
        Cloth existingCloth = clothRepositoryService.findById(clothId);

        // 이미지 업로드 처리
        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        // 엔티티의 업데이트 메서드 호출
        // DTO에서 필요한 값을 꺼내서 업데이트 메서드에 전달
        existingCloth.updateCloth(
                request.getName(),
                request.getSeasons(),
                request.getTempUpperBound(),
                request.getTempLowerBound(),
                request.getThicknessLevel(),
                request.getVisibility(),
                request.getClothUrl(),
                request.getBrand(),
                categoryId,
                imageUrl
        );
    }

    @Transactional
    public void deleteClothById(Long clothId){
        //Cloth 연관 엔티티 우선 삭제
        historyClothRepositoryService.deleteAllByClothId(clothId);
        clothFolderRepositoryService.deleteAllByClothId(clothId);
        clothImageRepositoryService.deleteByClothId(clothId);
        clothRepositoryService.deleteById(clothId);
    }
}
