package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryCloth;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryClothRepository extends JpaRepository<HistoryCloth, Long> {

    @Transactional
    @Modifying
    void deleteAllByClothId(@Param("clothId") Long clothId);

    @Transactional
    @Modifying
    @Query("DELETE FROM HistoryCloth hc WHERE hc.history = :history AND hc.cloth = :cloth")
    void deleteByHistoryAndCloth(@Param("history") History history, @Param("cloth") Cloth cloth);


    @Query("SELECT hc.cloth.id FROM HistoryCloth hc WHERE hc.history.id = :historyId")
    List<Long> findClothIdsByHistoryId(@Param("historyId") Long historyId);
}
