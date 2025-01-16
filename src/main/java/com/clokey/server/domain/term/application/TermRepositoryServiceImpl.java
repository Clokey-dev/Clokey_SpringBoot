package com.clokey.server.domain.term.application;

import com.clokey.server.domain.model.entity.Term;
import com.clokey.server.domain.model.repository.TermRepository;
import com.clokey.server.domain.term.exception.TermException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermRepositoryServiceImpl implements TermRepositoryService{

    private final TermRepository termRepository;

    @Override
    public boolean termExist(Long termId) {
        return termRepository.existsById(termId);
    }

    @Override
    public Term findById(Long termId) {
        return termRepository.findById(termId)
                .orElseThrow(() -> new TermException(ErrorStatus.NO_SUCH_TERM));

    }
}
