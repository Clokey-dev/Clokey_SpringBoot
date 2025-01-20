package com.clokey.server.domain.cloth.dao;

import com.clokey.server.domain.model.Cloth;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClothRepository extends JpaRepository<Cloth, Long> {

    @EntityGraph(attributePaths = {"images"})
    Optional<Cloth> findById(Long id);

    @Override
    boolean existsById(Long aLong);
}
