package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dto.ClothRequestDto;
import com.clokey.server.domain.cloth.dto.ClothResponseDto;
import com.clokey.server.domain.model.entity.Cloth;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ClothService {

    boolean clothExist(Long clothId);

    boolean isPublic(Long clothId);

    //public ClothResponseDto.ClothCreateResult createCloth(ClothRequestDto.ClothCreateRequest request, MultipartFile imageFile);

    Optional<Cloth> readClothById(Long clothId);

    void deleteClothById(Long clothId);
}
