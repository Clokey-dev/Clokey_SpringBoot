package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    int countByHistory_Id(Long historyId);

    boolean existsByMember_IdAndHistory_Id(Long memberId, Long historyId);

    void deleteByMember_IdAndHistory_Id(Long memberId, Long historyId);

    @Modifying  // 수정/삭제 작업을 나타냄
    @Query("DELETE FROM MemberLike ml WHERE ml.history.id = :historyId")
    void deleteAllByHistoryId(@Param("historyId") Long historyId);
}
