package com.clokey.server.domain.MemberLike.dao;

import com.clokey.server.domain.model.mapping.MemberLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {
}
