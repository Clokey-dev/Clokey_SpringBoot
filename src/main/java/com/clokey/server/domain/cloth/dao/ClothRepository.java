package com.clokey.server.domain.cloth.dao;

import com.clokey.server.domain.model.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothRepository extends JpaRepository<Cloth, Long> {
}
