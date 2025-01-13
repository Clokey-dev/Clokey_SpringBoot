package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dao.MemberRepository;
import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberRepositoryServiceImpl implements MemberRepositoryService {

    private final MemberRepository memberRepository;

    @Override
    public boolean memberExist(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public Optional<Member> getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }
}

    @Override
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

}
