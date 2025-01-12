package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dao.MemberRepository;
import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRepositoryServiceImpl implements MemberRepositoryService {

    private final MemberRepository memberRepository;

    @Override
    public boolean memberExist(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new TermException(ErrorStatus.NO_SUCH_MEMBER));
    }
}



