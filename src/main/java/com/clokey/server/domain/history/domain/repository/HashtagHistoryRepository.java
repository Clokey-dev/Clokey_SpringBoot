package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);

}
