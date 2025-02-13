package com.clokey.server.domain.member.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    Optional <Member> findMemberByEmail(String email);

    List<Member> findInactiveUsersBefore(LocalDate cutoffDate);

    List<History> findHistoriesByMemberId(Long memberId);

    List<Cloth> findClothesByMemberId(Long memberId);

    List<Folder> findFoldersByMemberId(Long memberId);

    List<Comment> findCommentsByMemberId(Long memberId);

    void deleteMemberById(Long memberId);
}
