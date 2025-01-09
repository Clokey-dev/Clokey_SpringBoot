package com.clokey.server.domain.HistoryImage.dao;

import com.clokey.server.domain.model.HistoryImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryImageRepository extends JpaRepository<HistoryImage, Long> {
}
