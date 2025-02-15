package com.clokey.server.domain.term.application;

import com.clokey.server.domain.term.domain.entity.Term;
import com.clokey.server.domain.term.domain.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermRepositoryServiceImpl implements TermRepositoryService {
    private final TermRepository termRepository;

    @Override
    public List<Term> findByOptionalTrue() {
        return termRepository.findByOptionalTrue();
    }

    @Override
    public Term findById(Long id) {
        return termRepository.findById(id).orElse(null);
    }

    @Override
    public List<Term> findAll() {
        return termRepository.findAll();
    }
}

