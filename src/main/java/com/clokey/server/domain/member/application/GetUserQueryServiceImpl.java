package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.GetUserConverter;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.model.Member;
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
    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public MemberDTO.GetUserRP getUser(String clokeyId) {
        Member member = memberRepositoryService.findMemberByClokeyId(clokeyId);

        Long recordCount = countHistoryByMember(member);
        Long followerCount = countFollowersByMember(member);
        Long followingCount = countFollowingByMember(member);

        return GetUserConverter.toGetUserResponseDTO(member, recordCount, followerCount, followingCount);
    }

    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countHistoryByMember(Member member) {
        String jpql = "SELECT COUNT(h) FROM History h WHERE h.member = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countFollowersByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.followed = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }

    @Transactional(readOnly = true) // 트랜잭션 읽기 전용으로 설정
    public Long countFollowingByMember(Member member) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.following = :member";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("member", member);
        return query.getSingleResult();
    }
}




