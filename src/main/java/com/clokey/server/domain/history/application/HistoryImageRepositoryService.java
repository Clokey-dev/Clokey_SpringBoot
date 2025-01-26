package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.HistoryImage;

import java.util.List;

public interface HistoryImageRepositoryService {

    boolean existsByHistory_Id(Long historyId);

    List<HistoryImage> findByHistory_Id(Long historyId);

    HistoryImage save(HistoryImage historyImage);

    void deleteAllByHistory_Id(Long historyId);
}
