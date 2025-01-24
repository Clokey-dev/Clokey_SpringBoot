package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {
    @Override
    Member getReferenceById(Long aLong);
}
