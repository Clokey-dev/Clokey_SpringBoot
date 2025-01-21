package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Cloth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ClothRepository extends JpaRepository<Cloth, Long> {

    boolean existsById(Long id);

    @EntityGraph(attributePaths = {"images"})
    Optional<Cloth> findById(Long id);

    @Transactional
    @Modifying
    void deleteById(Long id);
}
