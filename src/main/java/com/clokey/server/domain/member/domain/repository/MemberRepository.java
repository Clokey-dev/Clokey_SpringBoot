package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {
    @Override
    Member getReferenceById(Long aLong);

    boolean existsByClokeyId(String clokeyId);

    Optional<Member> findByClokeyId(String clokeyId);

    @Query("SELECT m FROM Member m WHERE m.inactiveDate <= :cutoffDate")
    List<Member> findInactiveUsersBefore(LocalDate cutoffDate);

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId")
    List<History> findHistoriesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT h.id FROM History h WHERE h.member.id = :memberId")
    List<Long> findHistoryIdsByMemberId(@Param("memberId") Long memberId);


    @Query("SELECT c FROM Cloth c WHERE c.member.id = :memberId")
    List<Cloth> findClothsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Folder f WHERE f.member.id = :memberId")
    List<Folder> findFoldersByMemberId(@Param("memberId") Long memberId);

    void deleteById(Long memberId);

    @Query("SELECT c FROM Comment c WHERE c.member.id = :memberId")
    List<Comment> findCommentsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT n FROM ClokeyNotification n WHERE n.member.id = :memberId")
    List<ClokeyNotification> findNotificationsByMemberId(@Param("memberId") Long memberId);
}
