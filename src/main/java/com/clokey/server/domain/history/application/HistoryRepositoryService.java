package com.clokey.server.domain.history.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Visibility;

public interface HistoryRepositoryService {

    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    List<History> findHistoriesByMemberWithinWeek(Long memberId);

    void incrementLikes(Long historyId);

    void decrementLikes(Long historyId);

    History findById(Long historyId);

    boolean existsById(Long historyId);

    History save(History history);

    boolean checkHistoryExistOfDate(LocalDate date, Long memberId);

    History getHistoryOfDate(LocalDate date, Long memberId);

    void deleteById(Long historyId);

    List<Boolean> existsByHistoryDateAndMemberIds(LocalDate historyDate, List<Long> memberIds);

    void deleteByHistoryIds(List<Long> historyIds);

    Page<History> findByMemberInAndVisibilityOrderByHistoryDateDesc(List<Member> members, Visibility visibility, Pageable pageable);

    List<History> findTop6ByMemberInAndVisibilityOrderByHistoryDateDesc(List<Member> member, Visibility visibility);

    List<History> findTop10MembersByHashtagIdsOrderByLikes(List<Long> hashtagIds, Long currentMemberId);

    List<History> findAll();

    Long countHistoryByMember(Member member);


}
