package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.model.entity.ClothImage;
import com.clokey.server.domain.model.repository.ClothFolderRepository;
import com.clokey.server.domain.model.repository.ClothImageRepository;
import com.clokey.server.domain.model.repository.ClothRepository;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.repository.HistoryClothRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.clokey.server.global.infra.s3.S3ImageService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;
    private final ClothImageRepository clothImageRepository;
    private final ClothFolderRepository clothFolderRepository;
    private final HistoryClothRepository historyClothRepository;
    private final ClothAccessibleValidator clothAccessibleValidator;
    private final S3ImageService s3ImageService;

    // 옷ID로 Details 조회 후 DTO로 변환해서 반환
    public ClothResponseDTO.ClothReadResult readClothDetailsById(Long clothId, Long memberId) {

        // Cloth 레포지토리 조회
        Optional<Cloth> cloth = clothRepository.findById(clothId);

        // 다른 유저의 옷이고, 비공개인 유저인지 확인하는 validator
        clothAccessibleValidator.validateMemberAccessOfMember(cloth.get().getMemberId(), memberId);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothReadResult(cloth.get());
    }

    // 카테고리ID와 멤버ID로 PreView 조회 후 DTO로 변환해서 반환
//    public ClothResponseDTO.CategoryClothReadResult readClothPreviewByCategoryAndMember(Long memberId, Long categoryId) {
//    }

    @Transactional
    public ClothResponseDTO.ClothCreateOrUpdateResult createCloth(Long memberId,
                                                                  ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                                                  MultipartFile imageFile) {

        // Cloth 엔티티 생성 후 요청 정보 반환해서 저장
        Cloth cloth = clothRepository.save(ClothConverter.toCloth(memberId,request));

        // 이미지 업로드 후 URL 반환
        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        // ClothImage 엔티티 생성 & URL 저장
        ClothImage clothImage = ClothImage.builder()
                .imageUrl(imageUrl)
                .cloth(cloth)
                .build();

        // ClothImage 저장
        clothImageRepository.save(clothImage);

        // Cloth를 응답형식로 변환하여 반환
        return ClothConverter.toClothCreateOrUpdateResult(cloth);
    }

    /*

    @Transactional
    public ClothResponseDTO.ClothCreateOrUpdateResult updateClothById(Long clothId,
                                                                      Long memberId,
                                                                      ClothRequestDTO.ClothCreateOrUpdateRequest request,
                                                                      MultipartFile imageFile){

        // 기존 Cloth 조회
        Optional<Cloth> existingCloth = clothRepository.findById(clothId);

        // 이미지 업로드 처리
        String imageUrl = (imageFile != null) ? s3ImageService.upload(imageFile) : null;

        // 엔티티의 업데이트 메서드 호출
        existingCloth.get().updateCloth(request, imageUrl);

        // Cloth를 응답 DTO로 변환하여 반환
        return ClothConverter.toClothCreateOrUpdateResult(existingCloth.get());
    }

    @Transactional
    public void deleteClothById(Long clothId){
        //Cloth 연관 엔티티 우선 삭제
        historyClothRepository.deleteAllByClothId(clothId);
        clothFolderRepository.deleteAllByClothId(clothId);
        clothImageRepository.deleteByClothId(clothId);
        clothRepository.deleteById(clothId);
    }
}
