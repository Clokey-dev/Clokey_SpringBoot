package com.clokey.server.domain.term.converter;

import com.clokey.server.domain.model.entity.Term;
import com.clokey.server.domain.term.dto.TermResponseDTO;

public class TermConverter {

    // Term -> TermResponseDTO.TermDto 변환
    public static TermResponseDTO.Term toTermDto(Term term, Boolean agreed) {
        return TermResponseDTO.Term.builder()
                .termId(term.getId())
                .agreed(agreed)
                .build();
    }
}

