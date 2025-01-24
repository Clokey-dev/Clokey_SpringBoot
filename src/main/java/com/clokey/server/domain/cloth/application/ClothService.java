package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClothService {

    ClothResponseDTO.ClothReadResult readClothDetailsById(Long clothId, Long memberId);

    //ClothResponseDTO.CategoryClothReadResult readClothPreViewByCategoryId(Long categoryId, Long memberId);

    ClothResponseDTO.ClothCreateResult createCloth(ClothRequestDTO.ClothCreateRequest request, MultipartFile imageFile);

    //ClothResponseDTO.ClothUpdateResult updateClothById(ClothRequestDTO.ClothUpdateRequest request, MultipartFile imageFile);

    void deleteClothById(Long clothId);
}
