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
    public static RecommendationResponseDTO.Recommend toRecommendDTO(String imageUrl, String subtitle, String hashtag) {
        return new RecommendationResponseDTO.Recommend(
                imageUrl,
                subtitle,
                hashtag,
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
                            date
                    );
                })
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate()))
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.Calendar> toCalendarDTO(Page<History> historyPage, Map<History, List<String>> historyImageMap) {
        return historyPage.getContent().stream()
                .map(history -> {
                    Member historyOwner = history.getMember();
                    List<String> imageUrls = historyImageMap.getOrDefault(history, Collections.emptyList());
                    String imageUrl = imageUrls.isEmpty() ? null : imageUrls.get(0); // 첫 번째 이미지 사용

                    return new RecommendationResponseDTO.Calendar(
                            history.getHistoryDate(),
                            historyOwner.getClokeyId(),
                            historyOwner.getProfileImageUrl(),
                            history.getId(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.People> toPeopleDTO(List<History> recommendedhistories, Map<Long, String> historyImageMap) {
        return recommendedhistories.stream()
                .distinct()
                .limit(4)
                .map(recommendedHistory -> new RecommendationResponseDTO.People(
                        recommendedHistory.getMember().getClokeyId(),
                        recommendedHistory.getMember().getProfileImageUrl(),
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

    public static RecommendationResponseDTO.DailyNewsResult toDailyNewsResult(List<RecommendationResponseDTO.Recommend> recommendList, List<RecommendationResponseDTO.Closet> closetList, List<RecommendationResponseDTO.Calendar> calendarList, List<RecommendationResponseDTO.People> peopleList) {
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
}
