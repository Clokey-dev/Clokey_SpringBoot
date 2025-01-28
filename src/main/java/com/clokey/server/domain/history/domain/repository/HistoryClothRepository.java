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

    // 특정 HistoryId에 연결된 모든 Cloth 조회
    @Query("SELECT hc.cloth FROM HistoryCloth hc WHERE hc.history.id = :historyId")
    List<Cloth> findAllClothsByHistoryId(@Param("historyId") Long historyId);

    // 특정 HistoryId에 연결된 모든 HistoryCloth 삭제
    @Modifying
    @Query("DELETE FROM HistoryCloth hc WHERE hc.history.id = :historyId")
    void deleteAllByHistoryId(@Param("historyId") Long historyId);
}
