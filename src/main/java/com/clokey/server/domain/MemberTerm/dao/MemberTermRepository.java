package com.clokey.server.domain.MemberTerm.dao;

import com.clokey.server.domain.model.mapping.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
