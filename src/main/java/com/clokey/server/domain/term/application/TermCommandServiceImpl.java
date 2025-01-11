package com.clokey.server.domain.term.application;


import com.clokey.server.domain.MemberTerm.dao.MemberTermRepository;
import com.clokey.server.domain.member.dao.MemberRepository;
import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.model.Term;
import com.clokey.server.domain.model.mapping.MemberTerm;
import com.clokey.server.domain.term.converter.TermConverter;
import com.clokey.server.domain.term.dao.TermRepository;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.ErrorState;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermCommandServiceImpl implements TermCommandService {

    private final MemberRepository memberRepository;
    private final TermRepository termRepository;
    private final MemberTermRepository memberTermRepository;

    @Override
    @Transactional
    public TermResponseDTO joinTerm(Long userId, TermRequestDTO.JoinDto request) {
        // 사용자 정보 확인
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new TermException(ErrorStatus.NO_SUCH_MEMBER));

        // 약관 처리
        List<TermResponseDTO.TermDto> termResponses = new ArrayList<>();
        for (TermRequestDTO.JoinDto.TermDto termDto : request.getTerms()) {
            Term term = termRepository.findById(termDto.getTermId())
                    .orElseThrow(() -> new TermException(ErrorStatus.NO_SUCH_TERM));

            // 필수 약관 확인
            if (!term.getOptional() && !termDto.getAgreed()) {
                throw new TermException(ErrorStatus.ESSENTIAL_TERM_NOT_AGREED);
            }

            // MemberTerm 생성 및 저장
            MemberTerm memberTerm = MemberTerm.builder()
                    .member(member)
                    .term(term)
                    .build();

            memberTermRepository.save(memberTerm);

            // 응답 데이터에 추가
            termResponses.add(TermResponseDTO.TermDto.builder()
                    .termId(term.getId())
                    .agreed(termDto.getAgreed())
                    .build());
        }

        // 최종 응답 생성
        return TermResponseDTO.builder()
                .userId(userId)
                .terms(termResponses)
                .build();
    }
}

