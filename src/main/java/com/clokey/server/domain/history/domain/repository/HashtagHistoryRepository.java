package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashtagHistoryRepository extends JpaRepository<HashtagHistory, Long> {

    boolean existsByHistory_Id(Long historyId);

    List<HashtagHistory> findByHistory_Id(Long historyId);

    @Transactional
    @Modifying
    @Query("DELETE FROM HashtagHistory hh WHERE hh.hashtag = :hashtag AND hh.history = :history")
    void deleteByHashtagAndHistory(@Param("hashtag") Hashtag hashtag, @Param("history") History history);

    @Transactional
    @Modifying
    @Query("DELETE FROM HashtagHistory hh WHERE hh.history.id = :historyId")
    void deleteAllByHistoryId(@Param("historyId") Long historyId);
}
