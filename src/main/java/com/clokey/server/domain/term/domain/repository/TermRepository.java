package com.clokey.server.domain.term.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.clokey.server.domain.term.domain.entity.Term;

public interface TermRepository extends JpaRepository<Term, Long> {
    List<Term> findByOptionalTrue(); // 선택 약관만 조회하는 메서드
}
