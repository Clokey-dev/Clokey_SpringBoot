package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.GetUserConverter;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class GetUserQueryServiceImpl implements GetUserQueryService {

    private final MemberRepositoryService memberRepositoryService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public MemberDTO.GetUserRP getUser(String clokeyId, Member currentUser) { // 현재 사용자 추가

        Member member;
        Boolean isFollowing;

        if(clokeyId == null){
            member = currentUser;
            isFollowing = null;
        } else {
            member = memberRepositoryService.findMemberByClokeyId(clokeyId);
            isFollowing = isFollowing(currentUser, member); // 팔로우 여부 체크 추가
        }

        Long recordCount = countHistoryByMember(member);
        Long followerCount = countFollowersByMember(member);
        Long followingCount = countFollowingByMember(member);
        List<String> topClothImages=getTop3ClothImages(member);

        return GetUserConverter.toGetUserResponseDTO(
                member, recordCount, followerCount, followingCount, isFollowing,
                topClothImages.size() > 0 ? topClothImages.get(0) : null,
                topClothImages.size() > 1 ? topClothImages.get(1) : null,
                topClothImages.size() > 2 ? topClothImages.get(2) : null
        );
    }

    @Transactional(readOnly = true)
    public Boolean isFollowing(Member currentUser, Member targetUser) {
        String jpql = "SELECT COUNT(f) FROM Follow f WHERE f.following = :currentUser AND f.followed = :targetUser";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("currentUser", currentUser);
        query.setParameter("targetUser", targetUser);
        return query.getSingleResult() > 0;
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

    @Transactional(readOnly = true)
    public List<String> getTop3ClothImages(Member member) {
        String jpql = """
        SELECT c.image.imageUrl
        FROM Cloth c
        WHERE c.member = :member
        ORDER BY c.wearNum DESC
    """;

        TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
        query.setParameter("member", member);
        query.setMaxResults(3); // 상위 3개만 가져오기

        return query.getResultList();
    }
}




