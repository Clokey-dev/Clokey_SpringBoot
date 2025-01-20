package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.model.repository.ClothRepository;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.RequiredArgsConstructor;
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
