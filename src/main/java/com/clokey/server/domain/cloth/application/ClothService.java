package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ClothService {

    ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId, Long memberId);

    ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId, Long memberId);

    ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId, Long memberId);

    //ClothResponseDTO.CategoryClothReadResult readClothPreViewByCategoryId(Long categoryId, Long memberId);

    ClothResponseDTO.ClothCreateResult createCloth(Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void updateClothById(Long clothId, Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void deleteClothById(Long clothId);
}
