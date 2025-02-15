package com.clokey.server.domain.term.application;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.term.converter.TermConverter;
import com.clokey.server.domain.term.domain.entity.Term;
import com.clokey.server.domain.term.domain.entity.MemberTerm;
import com.clokey.server.domain.term.domain.repository.MemberTermRepository;
import com.clokey.server.domain.term.domain.repository.TermRepository;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


            // MemberTerm 생성 및 저장
            MemberTerm memberTerm = MemberTerm.builder()
                    .member(member)
                    .term(term)
                    .build();

            memberTermRepositoryService.save(memberTerm);

            // 응답 데이터에 추가
            termResponses.add(TermResponseDTO.Term.builder()
                    .termId(term.getId())
                    .agreed(termDto.getAgreed())
                    .build());
        }

        if(member.getRegisterStatus()==RegisterStatus.NOT_AGREED) {
            // 약관 동의가 완료되었으므로 회원의 등록 상태를 업데이트
            member.updateRegisterStatus(RegisterStatus.AGREED_PROFILE_NOT_SET);

            // 저장 (등록 상태 업데이트 반영)
            memberRepositoryService.saveMember(member);
        }

        // 최종 응답 생성
        return TermResponseDTO.builder()
                .userId(userId)
                .terms(termResponses)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<TermResponseDTO.TermList> getTerms() {
        List<Term> terms = termRepositoryService.findAll();  // 모든 약관 조회

        List<TermResponseDTO.TermList> termList = new ArrayList<>();
        for (Term term : terms) {
            termList.add(TermConverter.toTermListDto(term));
        }

        return termList;
    }

    @Override
    @Transactional(readOnly = true)
    public TermResponseDTO.UserAgreementDTO getOptionalTerms(Long userId) {
        // 사용자 조회
        Member member = memberRepositoryService.findMemberById(userId);

        // 사용자가 동의한 약관 조회
        List<MemberTerm> memberTerms = memberTermRepositoryService.findByMember(member);

        // 사용자가 동의한 약관 ID 목록
        Set<Long> agreedTermIds = memberTerms.stream()
                .map(memberTerm -> memberTerm.getTerm().getId())
                .collect(Collectors.toSet());

        // 전체 선택 약관 조회 (optional = true)
        List<Term> optionalTerms = termRepositoryService.findByOptionalTrue();

        // OptionalTermDTO 리스트 생성
        List<TermResponseDTO.OptionalTermDTO> termResponses = optionalTerms.stream()
                .map(term -> TermResponseDTO.OptionalTermDTO.builder()
                        .termId(term.getId())  // 약관 ID
                        .title(term.getTitle())  // 약관 제목
                        .agreed(agreedTermIds.contains(term.getId())) // 사용자가 동의했는지 여부 판단
                        .build())
                .collect(Collectors.toList());

        return TermResponseDTO.UserAgreementDTO.builder()
                .email(member.getEmail()) // 이메일 추가
                .appVersion("1.0.0") // 앱 버전 추가 (필드 확인 필요)
                .terms(termResponses)  // OptionalTermDTO 리스트 반환
                .build();
    }

    @Override
    @Transactional
    public TermResponseDTO.UserAgreementDTO optionalTermAgree(Long userId, TermRequestDTO.Join request) {
        // 사용자 조회
        Member member = memberRepositoryService.findMemberById(userId);

        // 약관 동의 처리
        List<TermResponseDTO.OptionalTermDTO> termResponses = new ArrayList<>();
        for (TermRequestDTO.Join.Term termDto : request.getTerms()) {
            // 약관 조회
            Term term = termRepositoryService.findById(termDto.getTermId());

            if (termDto.getAgreed()) {
                // 동의한 경우 -> 저장
                MemberTerm memberTerm = MemberTerm.builder()
                        .member(member)
                        .term(term)
                        .build();
                memberTermRepositoryService.save(memberTerm);
            } else {
                // 동의 철회한 경우 -> 삭제
                memberTermRepositoryService.deleteByMemberIdAndTermId(userId, term.getId());
            }

            // 응답 데이터 생성
            termResponses.add(TermResponseDTO.OptionalTermDTO.builder()
                    .termId(term.getId())  // 약관 ID
                    .title(term.getTitle())  // 약관 제목
                    .agreed(termDto.getAgreed())  // 실제 동의 여부 반영
                    .build());
        }

        // 최종 응답 생성
        return TermResponseDTO.UserAgreementDTO.builder()
                .email(member.getEmail()) // 이메일 추가
                .appVersion("1.0.0") // 앱 버전 추가
                .terms(termResponses)  // OptionalTermDTO 리스트 반환
                .build();
    }







}
