package com.clokey.server.domain.term.converter;

import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.model.Term;
import com.clokey.server.domain.model.mapping.MemberTerm;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;

public class TermConverter {

    // Term -> TermResponseDTO.TermDto 변환
    public static TermResponseDTO.TermDto toTermDto(Term term, Boolean agreed) {
        return TermResponseDTO.TermDto.builder()
                .termId(term.getId())
                .agreed(agreed)
                .build();
    }
}

