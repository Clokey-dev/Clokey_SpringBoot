package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.mapping.HistoryCloth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long> {

    @Transactional
    @Modifying
    void deleteAllByClothId(@Param("clothId") Long clothId);
}
