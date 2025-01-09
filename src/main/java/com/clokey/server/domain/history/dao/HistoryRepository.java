package com.clokey.server.domain.history.dao;

import com.clokey.server.domain.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
