package com.clokey.server.domain.HistoryCloth.dao;

import com.clokey.server.domain.model.mapping.HistoryCloth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long> {
}
