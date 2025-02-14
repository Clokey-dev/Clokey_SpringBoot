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

    @Query("SELECT h.name FROM Hashtag h WHERE h.id NOT IN " +
            "(SELECT hh.hashtag.id FROM HashtagHistory hh JOIN hh.history h WHERE h.member.id = :memberId) " +
            "ORDER BY RAND() LIMIT 1")
    Optional<String> findRandomUnusedHashtag(@Param("memberId") Long memberId);
}
