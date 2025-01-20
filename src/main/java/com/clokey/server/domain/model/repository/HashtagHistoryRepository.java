package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Hashtag;
import com.clokey.server.domain.model.entity.HistoryImage;
import com.clokey.server.domain.model.entity.mapping.HashtagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);

}
