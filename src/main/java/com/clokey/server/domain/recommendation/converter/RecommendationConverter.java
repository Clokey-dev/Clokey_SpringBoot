package com.clokey.server.domain.recommendation.converter;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecommendationConverter {
    public static RecommendationResponseDTO.RecommendCacheResult toRecommendCacheDTO(String imageUrl, Long memberId, String subtitle, String hashtag) {
        return new RecommendationResponseDTO.RecommendCacheResult(
                imageUrl,
                memberId,
                subtitle,
                hashtag,
                LocalDateTime.now()
        );
    }

    public static List<RecommendationResponseDTO.ClosetResult> toClosetDTO(Map<Pair<Member, LocalDate>, List<Cloth>> groupedClothes) {
        return groupedClothes.entrySet().stream()
                .map(entry -> {
                    Member closetOwner = entry.getKey().getFirst();
                    LocalDate date = entry.getKey().getSecond();
                    List<Cloth> groupedClothList = entry.getValue();

                    List<Long> clothesIds = groupedClothList.stream()
                            .map(Cloth::getId)
                            .collect(Collectors.toList());

                    List<String> images = groupedClothList.stream()
                            .map(cloth -> cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                            .collect(Collectors.toList());

                    return new RecommendationResponseDTO.ClosetResult(
                            closetOwner.getClokeyId(),
                            closetOwner.getProfileImageUrl(),
                            clothesIds,
                            images,
                            date
                    );
                })
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.ClosetCacheResult> toClosetCacheDTO(Map<Pair<Member, LocalDate>, List<Cloth>> groupedClothes) {
        return groupedClothes.entrySet().stream()
                .map(entry -> {
                    Member closetOwner = entry.getKey().getFirst();
                    LocalDate date = entry.getKey().getSecond();
                    List<Cloth> groupedClothList = entry.getValue();

                    List<Long> clothesIds = groupedClothList.stream()
                            .map(Cloth::getId)
                            .collect(Collectors.toList());

                    List<String> images = groupedClothList.stream()
                            .map(cloth -> cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                            .collect(Collectors.toList());

                    return new RecommendationResponseDTO.ClosetCacheResult(
                            closetOwner.getId(),
                            clothesIds,
                            images,
                            date
                    );
                })
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.CalendarResult> toCalendarDTO(Page<History> historyPage, Map<History, List<String>> historyImageMap) {
        return historyPage.getContent().stream()
                .map(history -> {
                    Member historyOwner = history.getMember();
                    List<String> imageUrls = historyImageMap.getOrDefault(history, Collections.emptyList());
                    String imageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0); // 첫 번째 이미지 사용

                    return new RecommendationResponseDTO.CalendarResult(
                            history.getHistoryDate(),
                            historyOwner.getClokeyId(),
                            historyOwner.getProfileImageUrl(),
                            history.getId(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.CalendarCacheResult> toCalendarCacheDTO(List<History> historyPage, Map<History, List<String>> historyImageMap) {
        return historyPage.stream()
                .map(history -> {
                    Member historyOwner = history.getMember();
                    List<String> imageUrls = historyImageMap.getOrDefault(history, Collections.emptyList());
                    String imageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0);

                    return new RecommendationResponseDTO.CalendarCacheResult(
                            historyOwner.getId(),
                            history.getHistoryDate(),
                            history.getId(),
                            imageUrl
                    );
                })
                .limit(2)
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.PeopleResult> toPeopleDTO(List<History> recommendedHistories, Map<Long, String> historyImageMap) {
        return recommendedHistories.stream()
                .distinct()
                .limit(4)
                .map(recommendedHistory -> new RecommendationResponseDTO.PeopleResult(
                        recommendedHistory.getMember().getClokeyId(),
                        recommendedHistory.getMember().getProfileImageUrl(),
                        historyImageMap.get(recommendedHistory.getId()),
                        recommendedHistory.getId()
                ))
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.PeopleCacheResult> toPeopleCacheDTO(List<History> recommendedHistories, Map<Long, String> historyImageMap) {
        return recommendedHistories.stream()
                .distinct()
                .limit(4)
                .map(recommendedHistory -> new RecommendationResponseDTO.PeopleCacheResult(
                        recommendedHistory.getMember().getId(),
                        historyImageMap.get(recommendedHistory.getId()),
                        recommendedHistory.getId()
                ))
                .collect(Collectors.toList());
    }

    public static <T> RecommendationResponseDTO.DailyNewsAllResult<T> toDailyNewsAllResult(Page<T> page) {
        return RecommendationResponseDTO.DailyNewsAllResult.<T>builder()
                .dailyNewsResult(page.getContent())
                .totalPage(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }

    public static RecommendationResponseDTO.DailyNewsResult toDailyNewsResult(List<RecommendationResponseDTO.RecommendResult> recommendList, List<RecommendationResponseDTO.ClosetResult> closetList, List<RecommendationResponseDTO.CalendarResult> calendarList, List<RecommendationResponseDTO.PeopleResult> peopleList) {
        return RecommendationResponseDTO.DailyNewsResult.builder()
                .recommend(recommendList)
                .closet(closetList)
                .calendar(calendarList)
                .people(peopleList)
                .build();
    }

    public static RecommendationResponseDTO.LastYearHistoryResult toLastYearHistoryResult(Long historyId, List<String> historyImageUrls, Member member, Boolean isMine) {
        return RecommendationResponseDTO.LastYearHistoryResult.builder()
                .historyId(historyId)
                .nickName(member.getNickname())
                .imageUrls(historyImageUrls)
                .isMine(isMine)
                .build();
    }

    public static List<RecommendationResponseDTO.RecommendResult> convertRecommendToResponseDTO(
            List<RecommendationResponseDTO.RecommendCacheResult> cachedRecommend, Map<Long, Member> memberMap) {
        return cachedRecommend.stream()
                .map(recommendCache -> {
                    Member member = memberMap.get(recommendCache.getMemberId());
                    assert member != null;
                    return RecommendationResponseDTO.RecommendResult.builder()
                            .imageUrl(recommendCache.getImageUrl())
                            .subTitle(member.getNickname()+recommendCache.getSubTitle())
                            .hashtag(recommendCache.getHashtag())
                            .date(recommendCache.getDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.ClosetResult> convertClosetToResponseDTO(
            List<RecommendationResponseDTO.ClosetCacheResult> cachedClosets, Map<Long, Member> memberMap) {
        return cachedClosets.stream()
                .map(closetCacheDTO -> {
                    Member member = memberMap.get(closetCacheDTO.getMemberId());
                    return RecommendationResponseDTO.ClosetResult.builder()
                            .clokeyId(member != null ? member.getClokeyId() : "")
                            .profileImage(member != null ? member.getProfileImageUrl() : "")
                            .clothesId(closetCacheDTO.getClothesId())
                            .images(closetCacheDTO.getImages())
                            .date(closetCacheDTO.getDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.CalendarResult> convertCalendarToResponseDTO(
            List<RecommendationResponseDTO.CalendarCacheResult> cachedCalendars, Map<Long, Member> memberMap) {
        return cachedCalendars.stream()
                .map(calendarCacheDTO -> {
                    Member member = memberMap.get(calendarCacheDTO.getMemberId());
                    return RecommendationResponseDTO.CalendarResult.builder()
                            .clokeyId(member != null ? member.getClokeyId() : "")
                            .profileImage(member != null ? member.getProfileImageUrl() : "")
                            .date(calendarCacheDTO.getDate())
                            .historyId(calendarCacheDTO.getHistoryId())
                            .imageUrl(calendarCacheDTO.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.PeopleResult> convertPeopleToResponseDTO(
            List<RecommendationResponseDTO.PeopleCacheResult> cachedPeople, Map<Long, Member> memberMap) {
        return cachedPeople.stream()
                .map(peopleCacheDTO -> {
                    Member member = memberMap.get(peopleCacheDTO.getMemberId());
                    return RecommendationResponseDTO.PeopleResult.builder()
                            .clokeyId(member != null ? member.getClokeyId() : "")
                            .profileImage(member != null ? member.getProfileImageUrl() : "")
                            .imageUrl(peopleCacheDTO.getImageUrl())
                            .historyId(peopleCacheDTO.getHistoryId())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
