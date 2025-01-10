package com.clokey.server.domain.term.application;

import com.clokey.server.domain.term.dao.TermRepository;
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
}
