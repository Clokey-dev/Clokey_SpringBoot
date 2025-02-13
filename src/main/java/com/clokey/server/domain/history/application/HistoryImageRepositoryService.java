package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface HistoryImageRepositoryService {

    List<HistoryImage> findByHistoryId(Long historyId);

    void save(MultipartFile historyImage, History history);

    void save(List<MultipartFile> historyImages , History history);

    void deleteAllByHistoryId(Long historyId);

    List<HistoryImage> findByHistoryIdIn(List<Long> historyIds);

    Map<Long, String> findFirstImagesByHistoryIds(List<Long> historyIds);
}
