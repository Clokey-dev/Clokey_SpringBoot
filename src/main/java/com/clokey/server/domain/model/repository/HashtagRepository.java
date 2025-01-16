package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
