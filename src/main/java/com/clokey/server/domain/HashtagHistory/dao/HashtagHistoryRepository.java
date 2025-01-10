package com.clokey.server.domain.HashtagHistory.dao;

import com.clokey.server.domain.model.Hashtag;
import com.clokey.server.domain.model.HistoryImage;
import com.clokey.server.domain.model.mapping.HashtagHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    boolean existsByHistory_Id(Long historyId);

    List<Hashtag> findByHistory_Id(Long historyId);

}
