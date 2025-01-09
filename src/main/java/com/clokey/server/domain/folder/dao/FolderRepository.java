package com.clokey.server.domain.folder.dao;

import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
