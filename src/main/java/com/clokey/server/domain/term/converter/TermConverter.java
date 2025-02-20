package com.clokey.server.domain.term.converter;

import com.clokey.server.domain.term.domain.entity.Term;
import com.clokey.server.domain.term.dto.TermResponseDTO;

public class TermConverter {

    // Term -> TermResponseDTO.TermDto 변환
    public static TermResponseDTO.Term toTermDto(Term term, Boolean agreed) {
        return TermResponseDTO.Term.builder()
                .termId(term.getId())
                .agreed(agreed)
                .build();
    }

    // Term -> TermResponseDTO.TermList 변환
    public static TermResponseDTO.TermList toTermListDto(Term term) {
        return TermResponseDTO.TermList.builder()
                .termId(term.getId())
                .title(term.getTitle())  // 약관 제목
                .content(term.getBody())  // 약관 내용
                .optional(term.getOptional())  // optional이 아니면 필수 약관
                .build();
    }
}
