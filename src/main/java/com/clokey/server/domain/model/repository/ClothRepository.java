package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothRepository extends JpaRepository<Cloth, Long> {
}
