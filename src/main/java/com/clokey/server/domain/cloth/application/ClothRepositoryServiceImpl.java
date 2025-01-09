package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.dao.ClothRepository;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothRepositoryServiceImpl implements ClothRepositoryService{

    private final ClothRepository clothRepository;

    @Override
    public boolean clothExist(Long clothId) {
        return clothRepository.existsById(clothId);
    }

    //존재하지 않는 경우 exception 던져야함 get으로 임시 대체
    @Override
    public boolean canEdit(Long clothId, Long memberId) {
        Cloth cloth = clothRepository.findById(clothId).get();
        return cloth.getMember()
                .getId()
                .equals(memberId);
    }

    //존재하지 않는 경우 exception 던져야함 get으로 임시 대체
    @Override
    public Visibility getVisibility(Long clothId){
        return clothRepository.findById(clothId)
                .get()
                .getVisibility();
    }
}
