package com.clokey.server.domain.term.domain.repository;

import com.clokey.server.domain.term.domain.entity.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
