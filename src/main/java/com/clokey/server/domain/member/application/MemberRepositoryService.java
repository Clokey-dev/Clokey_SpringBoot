package com.clokey.server.domain.member.application;

import com.clokey.server.domain.model.Member;


import java.util.Optional;

public interface MemberRepositoryService {

    boolean memberExist(Long memberId);


    Member findMemberById(Long memberId);

    Member saveMember(Member member);
    Optional<Member> getMember(Long memberId);

    boolean idExist(String clokeyId);
}
