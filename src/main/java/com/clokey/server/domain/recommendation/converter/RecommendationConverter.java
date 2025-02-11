package com.clokey.server.domain.recommendation.converter;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecommendationConverter {
    public static RecommendationResponseDTO.Recommend toRecommendDTO(String imageUrl, String subtitle, String hashtag) {
        return new RecommendationResponseDTO.Recommend(
                imageUrl,
                subtitle,
                "#" + hashtag,
                LocalDateTime.now()
        );
    }

    public static List<RecommendationResponseDTO.Closet> toClosetDTO(Map<Pair<Member, LocalDate>, List<Cloth>> groupedClothes) {
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

                    return new RecommendationResponseDTO.Closet(
                            closetOwner.getClokeyId(),
                            closetOwner.getProfileImageUrl(),
                            clothesIds,
                            images,
                            date.atStartOfDay()
                    );
                })
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.Calendar> toCalendarDTO(Page<History> historyPage, Map<History, List<String>> historyImageMap) {
        Map<LocalDate, List<RecommendationResponseDTO.Event>> groupedEvents = historyPage.getContent().stream()
                .collect(Collectors.groupingBy(
                        History::getHistoryDate,
                        Collectors.mapping(history -> toEventDTO(history, historyImageMap), Collectors.toList())
                ));

        // 그룹핑된 데이터를 Calendar DTO 리스트로 변환
        return groupedEvents.entrySet().stream()
                .map(entry -> {
                    History sampleHistory = historyPage.getContent().stream()
                            .filter(h -> h.getHistoryDate().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);

                    if (sampleHistory == null) {
                        return null;
                    }

                    Member historyOwner = sampleHistory.getMember();

                    return new RecommendationResponseDTO.Calendar(
                            entry.getKey(),
                            historyOwner.getClokeyId(),
                            historyOwner.getProfileImageUrl(),
                            entry.getValue()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static RecommendationResponseDTO.Event toEventDTO(History history, Map<History, List<String>> historyImageMap) {
        List<String> images = historyImageMap.getOrDefault(history, List.of());
        String imageUrl = images.isEmpty() ? null : images.get(0);

        return new RecommendationResponseDTO.Event(
                history.getId(),
                imageUrl
        );
    }

    public static List<RecommendationResponseDTO.People> toPeopleDTO(List<Member> recommendedMembers) {
        return recommendedMembers.stream()
                .distinct()
                .limit(4)
                .map(recommendedMember -> new RecommendationResponseDTO.People(
                        recommendedMember.getClokeyId(),
                        recommendedMember.getProfileImageUrl(),
                        null
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

    public static RecommendationResponseDTO.DailyNewsResult toDailyNewsResult(List<RecommendationResponseDTO.Recommend> recommendList, List<RecommendationResponseDTO.Closet> closetList, List<RecommendationResponseDTO.Calendar> calendarList, List<RecommendationResponseDTO.People> peopleList) {
        return RecommendationResponseDTO.DailyNewsResult.builder()
                .recommend(recommendList)
                .closet(closetList)
                .calendar(calendarList)
                .people(peopleList)
                .build();
    }
}
