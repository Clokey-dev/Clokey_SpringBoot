package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryCloth;

public interface HistoryClothRepositoryService {

    void save(History history, Cloth cloth);

}
