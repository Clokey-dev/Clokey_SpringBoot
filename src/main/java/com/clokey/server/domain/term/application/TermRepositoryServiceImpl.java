package com.clokey.server.domain.term.application;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.term.domain.entity.Term;
import com.clokey.server.domain.term.domain.repository.TermRepository;

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

    @Override
    public boolean existsById(Long id) {
        return termRepository.existsById(id);
    }
}
