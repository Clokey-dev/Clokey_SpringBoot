package com.clokey.server.domain.term.application;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.domain.term.domain.entity.MemberTerm;
import com.clokey.server.domain.term.domain.entity.Term;
import com.clokey.server.domain.term.domain.repository.MemberTermRepository;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberTermRepositoryServiceImpl implements MemberTermRepositoryService {

    private final MemberTermRepository memberTermRepository;

    @Override
    public void deleteByMemberId(Long memberId) {
        memberTermRepository.deleteByMemberId(memberId); // Repository에서 처리
    }

    @Override
    public List<MemberTerm> findByMember(Member member) {
        return memberTermRepository.findByMember(member);
    }

    @Override
    public void deleteByMemberIdAndTermId(Long memberId, Long termId) {
        memberTermRepository.deleteByMemberIdAndTermId(memberId, termId);
    }

    @Override
    public MemberTerm save(MemberTerm memberTerm) {
        return memberTermRepository.save(memberTerm);
    }


}


