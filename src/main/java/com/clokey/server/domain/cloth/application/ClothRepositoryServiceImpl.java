package com.clokey.server.domain.cloth.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.domain.repository.ClothRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClothRepositoryServiceImpl implements ClothRepositoryService{

    private final ClothRepository clothRepository;

    @Override
    @EntityGraph(attributePaths = {"images"})
    public Cloth findById(Long id){
        return clothRepository.findById(id).orElseThrow(()->new DatabaseException(ErrorStatus.NO_SUCH_CLOTH));
    }

    @Override
    @Modifying
    public void deleteById(Long id){
        clothRepository.deleteById(id);
    }

    @Override
    public Cloth save(Cloth cloth) {
        return clothRepository.save(cloth);
    }

    @Override
    public boolean existsById(Long clothId) {
        return clothRepository.existsById(clothId);
    }

    @Override
    public List<Cloth> findAllById(List<Long> clothId) {
        return clothRepository.findAllById(clothId);
    }
}
