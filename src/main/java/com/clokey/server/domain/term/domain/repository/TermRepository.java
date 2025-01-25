package com.clokey.server.domain.term.domain.repository;

import com.clokey.server.domain.term.domain.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
