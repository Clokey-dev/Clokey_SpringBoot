package com.clokey.server.domain.term.application;

import com.clokey.server.domain.term.domain.entity.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepositoryService {

    List<Term> findByOptionalTrue();

    Term findById(Long id);

    List<Term> findAll();
}
