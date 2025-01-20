package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

}
