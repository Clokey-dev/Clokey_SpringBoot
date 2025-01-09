package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dao.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRepositoryServiceImpl implements MemberRepositoryService{

    private final MemberRepository memberRepository;

    @Override
    public boolean memberExist(Long memberId) {
        return memberRepository.existsById(memberId);
    }
}


