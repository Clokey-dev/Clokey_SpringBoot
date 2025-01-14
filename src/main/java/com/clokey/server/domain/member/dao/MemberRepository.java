package com.clokey.server.domain.member.dao;

import com.clokey.server.domain.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, Long> {

}
