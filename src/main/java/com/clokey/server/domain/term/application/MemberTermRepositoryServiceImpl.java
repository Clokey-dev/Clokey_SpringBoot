package com.clokey.server.domain.term.application;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.domain.term.domain.entity.MemberTerm;
import com.clokey.server.domain.term.domain.repository.MemberTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberTermRepositoryServiceImpl implements MemberTermRepositoryService {

    private final MemberTermRepository memberTermRepository;
    private final MemberRepository memberRepository;


    @Override
    public void deleteByMemberId(Long memberId) {
        memberTermRepository.deleteByMemberId(memberId); // Repository에서 처리
    }

    @Override
    public MemberTerm findMemberTermById(Long memberId) {
        return memberTermRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("MemberTerm not found with id " + memberId));
    }

}

