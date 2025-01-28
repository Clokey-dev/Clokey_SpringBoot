package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.MemberLike;

public interface MemberLikeRepositoryService {

    public int countByHistory_Id(Long historyId);

    public boolean existsByMember_IdAndHistory_Id(Long memberId, Long historyId);

    public void deleteByMember_IdAndHistory_Id(Long memberId, Long historyId);

    public void save(MemberLike memberLike);
}
