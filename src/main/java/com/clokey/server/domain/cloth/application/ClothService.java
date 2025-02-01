package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import org.springframework.web.multipart.MultipartFile;

public interface ClothService {

    ClothResponseDTO.ClothPopupViewResult readClothPopupInfoById(Long clothId);

    ClothResponseDTO.ClothEditViewResult readClothEditInfoById(Long clothId);

    ClothResponseDTO.ClothDetailViewResult readClothDetailInfoById(Long clothId);

    ClothResponseDTO.CategoryClothPreviewListResult readClothPreviewInfoListByClokeyId(String ownerClokeyId, Long requesterId, Long categoryId, Season season, ClothSort sort, int page, int pageSize);

    ClothResponseDTO.ClothCreateResult createCloth(Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void updateClothById(Long clothId, Long categoryId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void deleteClothById(Long clothId);
}
