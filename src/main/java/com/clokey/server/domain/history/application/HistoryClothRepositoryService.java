package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;

import java.util.List;

public interface HistoryClothRepositoryService {

    void save(History history, Cloth cloth);

    void delete(History history, Cloth cloth);

    List<Long> findClothIdsByHistoryId(Long historyId);
}
