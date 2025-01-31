package com.clokey.server.domain.term.application;

import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;

import java.util.List;

public interface TermCommandService {
    TermResponseDTO joinTerm(Long userId, TermRequestDTO.Join request);
    List<TermResponseDTO.TermList> getTerms();  // 모든 약관 조회 메서드 추가
}

