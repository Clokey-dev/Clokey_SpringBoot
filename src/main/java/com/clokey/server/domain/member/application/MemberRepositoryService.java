package com.clokey.server.domain.member.application;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.clokey.server.domain.member.domain.entity.Member;

public interface MemberRepositoryService {
    boolean memberExist(Long memberId);

    Member findMemberById(Long memberId);

    Member saveMember(Member member);

    Optional<Member> getMember(Long memberId);

    boolean idExist(String clokeyId);

    Member findMemberByClokeyId(String clokeyId);

    Member getReferencedById(Long memberId);

    boolean existsByClokeyId(String clokeyId);

    Member findByClokeyId(String clokeyId);

    Optional<Member> findMemberByEmail(String email);

    Member getMemberByEmail(String email);

    boolean existsByEmail(String email);

    List<Member> findInactiveUsersBefore(LocalDate cutoffDate);

    List<Long> findHistoryIdsByMemberId(Long memberId);

    List<Long> findClothIdsByMemberId(Long memberId);

    List<Long> findFolderIdsByMemberId(Long memberId);

    List<Long> findCommentIdsByMemberId(Long memberId);

    List<Long> findNotificationIdsByMemberId(Long memberId);

    void deleteMemberById(Long memberId);

    List<Member> findAll();

    Map<Long, Member> findMembersByIds(Set<Long> memberIds);
}
