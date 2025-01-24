package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.GetUserConverter;
import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GetUserQueryServiceImpl implements GetUserQueryService {

    private final MemberRepositoryService memberRepositoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public MemberResponseDTO.GetUserRP getUser(String clokeyId) {
        Member member = memberRepositoryService.findMemberByClokeyId(clokeyId);

        Long recordCount = countHistoryByMember(member);
        Long followerCount = countFollowersByMember(member);
        Long followingCount = countFollowingByMember(member);

        return GetUserConverter.toGetUserResponseDTO(member, recordCount, followerCount, followingCount);
    }

    private Long countHistoryByMember(Member member) {
        String jpql = "SELECT COUNT(h) FROM History h WHERE h.member = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    private Long countFollowersByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.followed = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    private Long countFollowingByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.following = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }
}



