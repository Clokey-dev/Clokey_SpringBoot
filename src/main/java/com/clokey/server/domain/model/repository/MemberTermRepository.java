package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.mapping.MemberTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermRepository extends JpaRepository<MemberTerm, Long> {
}
