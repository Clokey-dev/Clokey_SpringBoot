package com.clokey.server.domain.member.application;

import com.clokey.server.domain.model.Member;

public interface MemberRepositoryService {

    boolean memberExist(Long memberId);

    Member findMemberById(Long memberId);

    Member saveMember(Member member);

}
