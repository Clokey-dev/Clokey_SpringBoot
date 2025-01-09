package com.clokey.server.domain.HashtagHistory.dao;

import com.clokey.server.domain.model.mapping.HashtagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {
}
