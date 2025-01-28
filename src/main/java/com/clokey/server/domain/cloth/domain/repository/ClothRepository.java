package com.clokey.server.domain.cloth.domain.repository;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ClothRepository extends JpaRepository<Cloth, Long> {

    boolean existsById(Long id);

    @EntityGraph(attributePaths = {"image"})
    Optional<Cloth> findById(Long id);

    @Transactional
    @Modifying
    void deleteById(Long id);
}
