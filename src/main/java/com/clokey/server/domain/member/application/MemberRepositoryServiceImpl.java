package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberRepositoryServiceImpl implements MemberRepositoryService {

    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public boolean memberExist(Long memberId) {
        return memberRepository.existsById(memberId);
    }

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Optional<Member> getMember(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    @Transactional // 쓰기 트랜잭션
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public boolean idExist(String clokeyId) {
        String jpql = "SELECT COUNT(m) > 0 FROM Member m WHERE m.clokeyId = :clokeyId";
        TypedQuery<Boolean> query = entityManager.createQuery(jpql, Boolean.class);
        query.setParameter("clokeyId", clokeyId);
        return query.getSingleResult();
    }

    @Override
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public Member findMemberByClokeyId(String clokeyId) {
        String jpql = "SELECT m FROM Member m WHERE m.clokeyId = :clokeyId";
        TypedQuery<Member> query = entityManager.createQuery(jpql, Member.class);
        query.setParameter("clokeyId", clokeyId);

        return query.getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("클로키 아이디에 해당하는 사용자가 없습니다."));
    }

    @Override
    public Member getReferencedById(Long memberId) {
        return memberRepository.getReferenceById(memberId);
    }


    @Override
    public boolean existsByClokeyId(String clokeyId) {
        return memberRepository.existsByClokeyId(clokeyId);
    }

    @Override
    public Member findByClokeyId(String clokeyId) {
        return memberRepository.findByClokeyId(clokeyId).orElseThrow(()->new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
    }


    public Optional<Member> findMemberByEmail(String email) {
        String jpql = "SELECT m FROM Member m WHERE m.email = :email";
        TypedQuery<Member> query = entityManager.createQuery(jpql, Member.class);
        query.setParameter("email", email);

        return query.getResultStream().findFirst(); // Optional<Member> 반환
    }

    @Override
    public List<Member> findAll(){
        return memberRepository.findAll();
    }

}
