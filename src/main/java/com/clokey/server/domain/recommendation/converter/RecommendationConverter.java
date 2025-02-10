package com.clokey.server.domain.recommendation.converter;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
                            closetOwner.getId(),
                            closetOwner.getClokeyId(),
                            closetOwner.getProfileImageUrl(),
                            clothesIds,
                            images,
                            date.atStartOfDay()
                    );
                })
                .sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate())) // 최신순 정렬
                .collect(Collectors.toList());
    }

    public static List<RecommendationResponseDTO.Calendar> toCalendarDTO(Map<LocalDate, List<RecommendationResponseDTO.Event>> groupedEvents, Member member) {
        return groupedEvents.entrySet().stream()
                .map(entry -> new RecommendationResponseDTO.Calendar(
                        entry.getKey(),
                        member.getId(),
                        member.getClokeyId(),
                        member.getProfileImageUrl(),
                        entry.getValue()
                ))
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
                        recommendedMember.getId(),
                        recommendedMember.getClokeyId(),
                        recommendedMember.getProfileImageUrl(),
                        null
                ))
                .collect(Collectors.toList());
    }
}
