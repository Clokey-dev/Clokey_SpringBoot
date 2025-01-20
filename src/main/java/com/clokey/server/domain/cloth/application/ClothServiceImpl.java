package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.dao.ClothRepository;
import com.clokey.server.domain.cloth.dto.ClothResponseDto;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.enums.ThicknessLevel;
import com.clokey.server.domain.model.enums.Visibility;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClothServiceImpl implements ClothService {

    private final ClothRepository clothRepository;

    @Override
    public boolean clothExist(Long clothId) {
        System.out.println(clothId);
        return clothRepository.existsById(clothId);
    }

    @Override
    public boolean isPublic(Long clothId) {
        Visibility visibility = clothRepository.findById(clothId)
                .get()
                .getVisibility();
        return visibility.equals(Visibility.PUBLIC);
    }

    public Optional<Cloth> getClothById(Long clothId) {
        return clothRepository.findById(clothId);
    }

}
