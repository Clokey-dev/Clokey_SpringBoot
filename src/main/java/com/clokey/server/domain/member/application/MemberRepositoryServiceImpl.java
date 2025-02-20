package com.clokey.server.domain.member.application;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;

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
        return memberRepository.findById(memberId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public boolean idExist(String clokeyId) {
        return memberRepository.existsByClokeyId(clokeyId);
    }

    @Override
    public Member findMemberByClokeyId(String clokeyId) {
        return memberRepository.findByClokeyId(clokeyId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
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
        return memberRepository.findByClokeyId(clokeyId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
    }

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email); // Optional<Member> 반환
    }

    @Override
    public List<Member> findInactiveUsersBefore(LocalDate cutoffDate) {
        return memberRepository.findInactiveUsersBefore(cutoffDate);
    }


    @Override
    public List<Long> findHistoryIdsByMemberId(Long memberId) {
        return memberRepository.findHistoryIdsByMemberId(memberId);
    }


    @Override
    public void deleteMemberById(Long memberId) {
        memberRepository.deleteById(memberId);
    }


    @Override
    public List<Long> findClothIdsByMemberId(Long memberId) {
        return memberRepository.findClothIdsByMemberId(memberId);
    }

    @Override
    public List<Long> findFolderIdsByMemberId(Long memberId) {
        return memberRepository.findFolderIdsByMemberId(memberId);
    }

    @Override
    public List<Long> findCommentIdsByMemberId(Long memberId) {
        return memberRepository.findCommentIdsByMemberId(memberId);
    }

    @Override
    public List<Long> findNotificationIdsByMemberId(Long memberId) {
        return memberRepository.findNotificationIdsByMemberId(memberId);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public Map<Long, Member> findMembersByIds(Set<Long> memberIds) {
        List<Member> members = memberRepository.findByIdIn(memberIds);
        return members.stream().collect(Collectors.toMap(Member::getId, member -> member));
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.getMemberByEmail(email);
    }

}
