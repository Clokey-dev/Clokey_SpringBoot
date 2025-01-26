package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClothService {

    ClothResponseDTO.ClothReadResult readClothDetailsById(Long clothId, Long memberId);

    //ClothResponseDTO.CategoryClothReadResult readClothPreViewByCategoryId(Long categoryId, Long memberId);

    ClothResponseDTO.ClothCreateOrUpdateResult createCloth(Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    ClothResponseDTO.ClothCreateOrUpdateResult updateClothById(Long clothId, Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request, MultipartFile imageFile);

    void deleteClothById(Long clothId);
}
