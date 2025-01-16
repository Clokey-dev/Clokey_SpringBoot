package com.clokey.server.domain.member.application;

import com.clokey.server.domain.model.repository.MemberRepository;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberRepositoryServiceImpl implements MemberRepositoryService {

    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean memberExist(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    public Optional<Member> getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }


    @Override
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public boolean idExist(String clokeyId) {
        String jpql = "SELECT COUNT(m) > 0 FROM Member m WHERE m.clokeyId = :clokeyId";
        TypedQuery<Boolean> query = entityManager.createQuery(jpql, Boolean.class);
        query.setParameter("clokeyId", clokeyId);
        return query.getSingleResult();
    }

    @Override
    public Member findMemberByClokeyId(String clokeyId) {
        String jpql = "SELECT m FROM Member m WHERE m.clokeyId = :clokeyId";
        TypedQuery<Member> query = entityManager.createQuery(jpql, Member.class);
        query.setParameter("clokeyId", clokeyId);

        return query.getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("클로키 아이디에 해당하는 사용자가 없습니다."));
    }

}
