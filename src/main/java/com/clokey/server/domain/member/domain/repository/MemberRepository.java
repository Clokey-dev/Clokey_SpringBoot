package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    @Override
    Member getReferenceById(Long aLong);

    boolean existsByClokeyId(String clokeyId);

    Optional<Member> findByClokeyId(String clokeyId);

    List<Member> findAll();

    boolean existsByEmail(String email);

    Member getMemberByEmail(String email);

    Optional<Member> findMemberByEmail(String email);

}
