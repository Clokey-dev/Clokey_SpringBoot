package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.HistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {

    List<HistoryImage> findByHistory_Id(Long historyId);

    List<HistoryImage> findByHistory_IdIn(List<Long> historyIds);

    @Modifying
    @Query("DELETE FROM HistoryImage hi WHERE hi.history.id IN :historyIds")
    void deleteAllByHistoryIds(@Param("historyIds") List<Long> historyIds);


    @Modifying
    @Query("DELETE FROM HistoryImage hi WHERE hi.history.id IN :historyIds")
    void deleteByHistoryIds(@Param("historyIds") List<Long> historyIds);

    List<HistoryImage> findByHistoryIdIn(List<Long> historyIds);

}
