package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.folder.application.ClothFolderRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.application.HistoryClothRepositoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.clokey.server.global.infra.s3.S3ImageService;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepositoryService clothRepositoryService;
    private final ClothImageRepositoryService clothImageRepositoryService;
    private final ClothFolderRepositoryService clothFolderRepositoryService;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    private final ClothAccessibleValidator clothAccessibleValidator;
    private final S3ImageService s3ImageService;

    // 옷ID로 Details 조회 후 DTO로 변환해서 반환
    public ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId, Long memberId) {

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // 다른 유저의 옷이고, 비공개인 유저인지 확인하는 validator
        clothAccessibleValidator.validateMemberAccessOfMember(cloth.getMemberId(), memberId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothPopupViewResult(cloth);
    }

    public ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId, Long memberId){

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // 다른 유저의 옷이고, 비공개인 유저인지 확인하는 validator
        clothAccessibleValidator.validateMemberAccessOfMember(cloth.getMemberId(), memberId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothEditViewResult(cloth);
    }

    public ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId, Long memberId){

        // Cloth 레포지토리 조회
        Cloth cloth = clothRepositoryService.findById(clothId);

        // 다른 유저의 옷이고, 비공개인 유저인지 확인하는 validator
        clothAccessibleValidator.validateMemberAccessOfMember(cloth.getMemberId(), memberId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothDetailViewResult(cloth);
    }

    // 카테고리ID와 멤버ID로 PreView 조회 후 DTO로 변환해서 반환
//    public ClothResponseDTO.CategoryClothReadResult readClothPreviewByCategoryAndMember(Long memberId, Long categoryId) {
//    }

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
                                Long memberId,
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
