package com.clokey.server.domain.term.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.term.domain.entity.MemberTerm;

public interface MemberTermRepositoryService {

    void deleteByMemberId(Long memberId);
    MemberTerm findMemberTermById(Long memberId);

}
