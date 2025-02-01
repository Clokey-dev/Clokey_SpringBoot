package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import org.springframework.web.multipart.MultipartFile;

public interface ClothService {

    ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId, Long memberId);

    ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId, Long memberId);

    ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId, Long memberId);

    ClothResponseDTO.CategoryClothPreviewListResult readClothPreviewInfoListByClokeyId(String clokeyId, Long categoryId, Season season, ClothSort sort, int page, int pageSize);

    ClothResponseDTO.ClothCreateResult createCloth(Long categoryId, Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void updateClothById(Long clothId, Long categoryId, Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void deleteClothById(Long clothId);
}
