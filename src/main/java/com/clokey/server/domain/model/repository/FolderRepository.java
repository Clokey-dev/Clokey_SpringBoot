package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
