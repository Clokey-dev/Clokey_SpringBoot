package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.MemberLike;

public interface MemberLikeRepositoryService {

    int countByHistory_Id(Long historyId);

    boolean existsByMember_IdAndHistory_Id(Long memberId, Long historyId);

    void deleteByMember_IdAndHistory_Id(Long memberId, Long historyId);

    void save(MemberLike memberLike);

    void deleteAllByHistoryId(Long historyId);
}
