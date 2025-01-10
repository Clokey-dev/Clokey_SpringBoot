package com.clokey.server.domain.hashtag.dao;

import com.clokey.server.domain.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
