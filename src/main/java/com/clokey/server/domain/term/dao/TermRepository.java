package com.clokey.server.domain.term.dao;

import com.clokey.server.domain.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
