package com.clokey.server.domain.term.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.term.domain.entity.MemberTerm;

import java.util.List;

public interface MemberTermRepositoryService {

    List<MemberTerm> findByMember(Member member); // 특정 사용자의 동의한 약관 조회

    void deleteByMemberIdAndTermId(Long memberId, Long termId); // 특정 사용자의 특정 약관 동의 삭제

    MemberTerm save(MemberTerm memberTerm); // 사용자의 약관 동의 저장
}
