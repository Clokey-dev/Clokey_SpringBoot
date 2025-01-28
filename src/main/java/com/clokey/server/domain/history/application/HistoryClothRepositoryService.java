package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryClothRepositoryService {

    void save(History history, Cloth cloth);

    void delete(History history, Cloth cloth);

    List<Long> findClothIdsByHistoryId(Long historyId);

    void deleteAllByClothId(Long clothId);

    List<Cloth> findAllClothByHistoryId(Long historyId);

    void deleteAllByHistoryId(Long historyId);
}
