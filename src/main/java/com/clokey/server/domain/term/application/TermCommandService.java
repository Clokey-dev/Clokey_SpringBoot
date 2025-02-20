package com.clokey.server.domain.term.application;

import java.util.List;

import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;

public interface TermCommandService {
    TermResponseDTO joinTerm(Long userId, TermRequestDTO.Join request);

    List<TermResponseDTO.TermList> getTerms();  // 모든 약관 조회 메서드 추가

    TermResponseDTO.UserAgreementDTO getOptionalTerms(Long userId);  // 선택약관 동의 여부 조회 메서드 추가

    TermResponseDTO.UserAgreementDTO optionalTermAgree(Long userId, TermRequestDTO.Join request);  // 선택약관 동의 메서드 추가
}
