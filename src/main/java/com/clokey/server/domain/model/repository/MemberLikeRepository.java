package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.mapping.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

    int countByHistory_Id(Long historyId);

    boolean existsByMember_IdAndHistory_Id(Long memberId, Long historyId);

    void deleteByMember_IdAndHistory_Id(Long memberId, Long historyId);

}
