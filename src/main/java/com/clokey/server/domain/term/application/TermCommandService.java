package com.clokey.server.domain.term.application;

import com.clokey.server.domain.model.Term;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;

public interface TermCommandService {
    TermResponseDTO joinTerm(Long userId, TermRequestDTO.Join request);
}
