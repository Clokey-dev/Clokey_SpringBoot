package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothResponseDTO;

public interface ClothService {

    //public ClothResponseDto.ClothCreateResult createCloth(ClothRequestDto.ClothCreateRequest request, MultipartFile imageFile);

    ClothResponseDTO.ClothReadResult readClothDetailsById(Long clothId, Long memberId);

    void deleteClothById(Long clothId);
}
