package com.clokey.server.domain.term.application;

import java.util.List;

import com.clokey.server.domain.term.domain.entity.Term;

public interface TermRepositoryService {

    List<Term> findByOptionalTrue();

    Term findById(Long id);

    List<Term> findAll();

    boolean existsById(Long id);
}
