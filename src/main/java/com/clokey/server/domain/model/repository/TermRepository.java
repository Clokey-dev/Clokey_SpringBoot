package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermRepository extends JpaRepository<Term, Long> {
}
