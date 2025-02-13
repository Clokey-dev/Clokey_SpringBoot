package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;


import java.util.Optional;

public interface MemberRepositoryService {
    boolean memberExist(Long memberId);

    Member findMemberById(Long memberId);

    Member saveMember(Member member);
    Optional<Member> getMember(Long memberId);

    boolean idExist(String clokeyId);

    Member findMemberByClokeyId(String clokeyId);

    Member getReferencedById(Long memberId);

    boolean existsByClokeyId(String clokeyId);

    Member findByClokeyId(String clokeyId);

    Optional <Member> findMemberByEmail(String email);
}
