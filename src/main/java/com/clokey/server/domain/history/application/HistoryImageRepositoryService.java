package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HistoryImageRepositoryService {

    List<HistoryImage> findByHistoryId(Long historyId);

    void save(MultipartFile historyImage, History history);

    void save(List<MultipartFile> historyImages , History history);

    void deleteAllByHistoryId(Long historyId);

    void deleteAllByHistoryIds(List<Long> historyIds);
}
