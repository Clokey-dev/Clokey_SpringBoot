package com.clokey.server.domain.term.application;

import com.clokey.server.domain.MemberTerm.application.MemberTermRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.entity.Term;
import com.clokey.server.domain.model.entity.mapping.MemberTerm;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TermCommandServiceImpl implements TermCommandService {

    private final MemberRepositoryService memberRepositoryService;
    private final TermRepositoryService termRepositoryService;
    private final MemberTermRepositoryService memberTermRepositoryService;

    @Override
    @Transactional
    public TermResponseDTO joinTerm(Long userId, TermRequestDTO.Join request) {
        // 사용자 조회
        Member member = memberRepositoryService.findMemberById(userId);

        // 약관 처리
        List<TermResponseDTO.Term> termResponses = new ArrayList<>();
        for (TermRequestDTO.Join.Term termDto : request.getTerms()) {
            // 약관 조회 (이미 존재 여부는 확인된 상태)
            Term term = termRepositoryService.findById(termDto.getTermId());

            // 필수 약관 확인
            if (!term.getOptional() && !termDto.getAgreed()) {
                throw new TermException(ErrorStatus.ESSENTIAL_TERM_NOT_AGREED);
            }

            // MemberTerm 생성 및 저장
            MemberTerm memberTerm = MemberTerm.builder()
                    .member(member)
                    .term(term)
                    .build();

            memberTermRepositoryService.saveMemberTerm(memberTerm);

            // 응답 데이터에 추가
            termResponses.add(TermResponseDTO.Term.builder()
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
