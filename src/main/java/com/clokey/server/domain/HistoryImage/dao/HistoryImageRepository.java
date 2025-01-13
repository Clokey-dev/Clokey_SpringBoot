package com.clokey.server.domain.HistoryImage.dao;

import com.clokey.server.domain.model.HistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {

    boolean existsByHistory_Id(Long historyId);

    List<HistoryImage> findByHistory_Id(Long historyId);

}
