package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByName(String name);

    @Query("SELECT h FROM Hashtag h WHERE h.name IN :names")
    List<Hashtag> findHashtagsByNames(@Param("names") List<String> names);
}
