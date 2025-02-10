package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.history.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.history.application.HashtagRepositoryService;
import com.clokey.server.domain.history.application.HistoryImageRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.member.application.FollowRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepositoryService recommendationRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final ClothRepositoryService clothRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final FollowRepositoryService followRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final HashtagRepositoryService hashtagRepositoryService;


    @Override
    public RecommendationResponseDTO.DailyClothesResult getRecommendClothes(Long memberId, Float nowTemp) {
        return null;
    }

    @Override
    public RecommendationResponseDTO.DailyNewsResult getIssues(Long memberId, String view, String section, Integer page) {

        List<NewsType> requiredTypes = List.of(NewsType.RECOMMEND, NewsType.CLOSET, NewsType.CALENDAR, NewsType.PEOPLE);

        // 이미 저장된 news 조회 (한 번의 쿼리로 가져옴)
        List<Recommendation> existingNews = recommendationRepositoryService.findByMemberIdAndNewsTypeIn(memberId, requiredTypes);

        // 존재하지 않는 newsType 찾기
        Set<NewsType> existingTypes = existingNews.stream()
                .map(Recommendation::getNewsType)
                .collect(Collectors.toSet());

        List<Recommendation> newNewsList = new ArrayList<>();

        for (NewsType type : requiredTypes) {
            if (!existingTypes.contains(type)) {
                newNewsList.add(createDefaultRecommend(memberId, type));
            }
        }

        // 존재하지 않는 타입만 배치 INSERT
        if (!newNewsList.isEmpty()) {
            recommendationRepositoryService.saveAll(newNewsList);
            existingNews.addAll(newNewsList); // 리스트에 추가
        }

        // 요청된 view 및 section에 따라 변환
        return mapToResponse(existingNews, memberId, view, section, page);
    }

    private RecommendationResponseDTO.DailyNewsResult mapToResponse(List<Recommendation> recommendList, Long memberId, String view, String section, Integer page) {
        Member member = memberRepositoryService.findMemberById(memberId);

        List<Member> followingMembers = getFollowingMembers(member.getId());
        if ("simple".equals(view)) {
            return RecommendationResponseDTO.DailyNewsResult.builder()
                    .recommend(getRecommendList(member))
                    .closet(getClosetList(false, 1, followingMembers))
                    .calendar(getCalendarList(member, false, 1, followingMembers))
                    .people(getHotPeopleList(member))
                    .build();
        } else {
            if ("closet".equals(section)) {
                return RecommendationResponseDTO.DailyNewsResult.builder()
                        .closet(getClosetList(true, page, followingMembers))
                        .build();
            } else if ("calendar".equals(section)) {
                return RecommendationResponseDTO.DailyNewsResult.builder()
                        .calendar(getCalendarList(member, true, page, followingMembers))
                        .build();
            }
        }

        recommendationRepositoryService.saveAll(recommendList);
        return null;
    }


    // 추천 소식 조회 - 시도하지 않은 스타일, 최근에 태그한 해시태그, 자주 착용한 카테고리
    private List<RecommendationResponseDTO.Recommend> getRecommendList(Member member) {

        List<RecommendationResponseDTO.Recommend> recommendList = new ArrayList<>();

        // 시도하지 않은 스타일 - 랜덤 추천 hashtagRepositoryService에서 사용자의 기록들이 가지고 있는 해시태그들을 제외한 다른 해시태그 추천
        String unusedHashtag = hashtagRepositoryService.findRandomUnusedHashtag(member.getId());
        recommendList.add(new RecommendationResponseDTO.Recommend(
                getHistoryImageUrlByHashtagName(unusedHashtag),
                member.getNickname()+"이 시도하지 않은 스타일",
                "#" + unusedHashtag,
                LocalDateTime.now()
        ));


        // 최근에 태그한 해시태그 - 최근에 사용자가 기록에 태그한 해시태그 하나 반환
        String recentHashtag = hashtagHistoryRepositoryService.findLatestTaggedHashtag(member.getId());
        recommendList.add(new RecommendationResponseDTO.Recommend(
                getHistoryImageUrlByHashtagName(recentHashtag),
                member.getNickname() + "이 최근 태그한 해시태그",
                "#" + recentHashtag,
                LocalDateTime.now()
        ));

        // 자주 착용한 카테고리 - 사용자가 가장 많이 착용한 카테고리 하나 반환
        String frequentCategory = clothRepositoryService.findMostWornCategory(member.getId());
        recommendList.add(new RecommendationResponseDTO.Recommend(
                getHistoryImageUrlByHashtagName(frequentCategory),
                member.getNickname() + "이 자주 착용한 카테고리",
                "#" + frequentCategory,
                LocalDateTime.now()
        ));

        return recommendList;
    }

    // 팔로우 중인 옷장 업데이트 조회
    private List<RecommendationResponseDTO.Closet> getClosetList(boolean isFull, int page, List<Member> followingMembers) {

        // 팔로우한 멤버들의 최신 공개 옷 조회 (최신순 정렬)
        List<Cloth> clothesList = isFull
                ? clothRepositoryService.findByMemberInAndVisibilityOrderByCreatedAtDesc(followingMembers, Visibility.PUBLIC, PageRequest.of(page-1, 6)).getContent()
                : clothRepositoryService.findTop6ByMemberInAndVisibilityOrderByCreatedAtDesc(followingMembers, Visibility.PUBLIC);

        // 같은 날짜 + 같은 사용자가 올린 옷을 그룹화 (Map<Member + 날짜, List<Cloth>>)
        Map<Pair<Member, LocalDate>, List<Cloth>> groupedClothes = clothesList.stream()
                .collect(Collectors.groupingBy(
                        cloth -> Pair.of(cloth.getMember(), cloth.getCreatedAt().toLocalDate()) // 같은 사용자 + 같은 날짜 기준
                ));

        // 그룹화된 데이터를 `Closet` DTO로 변환
        return groupedClothes.entrySet().stream()
                .map(entry -> {
                    Member closetOwner = entry.getKey().getFirst(); // 해당 그룹의 멤버
                    LocalDate date = entry.getKey().getSecond(); // 해당 그룹의 날짜
                    List<Cloth> groupedClothList = entry.getValue(); // 같은 날짜에 같은 사용자가 올린 옷 리스트

                    //`clothesId`와 `images` 리스트로 묶기
                    List<Long> clothesIds = groupedClothList.stream()
                            .map(Cloth::getId)
                            .collect(Collectors.toList());

                    List<String> images = groupedClothList.stream()
                            .map(cloth -> Optional.ofNullable(cloth.getImage())
                                    .map(ClothImage::getImageUrl)
                                    .orElse(null))
                            .collect(Collectors.toList());

                    return new RecommendationResponseDTO.Closet(
                            closetOwner.getId(),
                            closetOwner.getClokeyId(),
                            closetOwner.getProfileImageUrl(),
                            clothesIds,
                            images,
                            date.atStartOfDay() // LocalDateTime 변환
                    );
                })
                .sorted(Comparator.comparing(RecommendationResponseDTO.Closet::getDate).reversed()) // 최신순 정렬
                .collect(Collectors.toList());
    }


    // 팔로우 중인 캘린더 업데이트 조회
    private List<RecommendationResponseDTO.Calendar> getCalendarList(Member member, boolean isFull, int page, List<Member> followedMembers) {
        // 팔로우한 멤버들의 최신 `History` 가져오기 (공개된 것만)
        List<History> historyList = isFull
                ? historyRepositoryService.findByMemberInAndVisibilityOrderByHistoryDateDesc(followedMembers, Visibility.PUBLIC, PageRequest.of(page-1, 6))
                : historyRepositoryService.findTop6ByMemberInAndVisibilityOrderByHistoryDateDesc(followedMembers, Visibility.PUBLIC);

        // `HistoryImage` 조회 (History ID 리스트 기반)
        List<Long> historyIds = historyList.stream().map(History::getId).toList();
        Map<History, List<String>> historyImageMap = historyImageRepositoryService.findByHistoryIdIn(historyIds)
                .stream()
                .collect(Collectors.groupingBy(
                        HistoryImage::getHistory,
                        Collectors.mapping(HistoryImage::getImageUrl, Collectors.toList()) // `imageUrl` 리스트 생성
                ));

        // 날짜별(`LocalDate`)로 그룹화하여 `RecommendationResponseDTO.Event` 리스트 생성
        Map<LocalDate, List<RecommendationResponseDTO.Event>> groupedEvents = historyList.stream()
                .collect(Collectors.groupingBy(
                        History::getHistoryDate, // 날짜 기준 그룹화
                        Collectors.mapping(
                                history -> {// 이미지 리스트에서 첫 번째 이미지 사용 (없으면 기본 이미지)
                                    List<String> images = historyImageMap.getOrDefault(history, List.of());
                                    String imageUrl = images.isEmpty() ? null: images.get(0);

                                    return new RecommendationResponseDTO.Event(
                                            history.getId(),
                                            imageUrl
                                    );
                                }, Collectors.toList()
                        )));

        // 그룹핑된 데이터를 `CalendarDTO` 리스트로 변환
        return groupedEvents.entrySet().stream()
                .map(entry -> new RecommendationResponseDTO.Calendar(
                        entry.getKey(), // 날짜
                        member.getId(), // 현재 사용자 ID
                        member.getClokeyId(), // 닉네임
                        member.getProfileImageUrl(), // 프로필 이미지
                        entry.getValue() // 이벤트 리스트
                ))
                .collect(Collectors.toList());
    }


    // Hot 계정 조회
    private List<RecommendationResponseDTO.People> getHotPeopleList(Member member) {
        //기록 최신 것부터 해시태그를 조회함. 해시태그 아이디를 hashtagHistoryRepository에서 찾아서 그 history의 주인들을 최대 네 명 추천해주는 로직.
        List<Long> hashtagIds = hashtagHistoryRepositoryService.findTop3HashtagIdsByMemberIdOrderByHistoryDateDesc(member.getId());

        if (hashtagIds.isEmpty()) {
            return List.of(); // 해시태그가 없으면 빈 리스트 반환
        }

        // 해당 해시태그를 사용한 다른 사용자 찾기 (최대 10명) + 좋아요 많은 순
        List<Member> recommendedMembers = historyRepositoryService.findTop10MembersByHashtagIdsOrderByLikes(hashtagIds, member.getId());

        // 중복 제거 및 최대 4명 추천
        return recommendedMembers.stream()
                .distinct() // 중복 제거
                .limit(4) // 최대 4명
                .map(recommendedMember -> new RecommendationResponseDTO.People(
                        recommendedMember.getId(),
                        recommendedMember.getClokeyId(),
                        recommendedMember.getProfileImageUrl(),
                        null
                ))
                .collect(Collectors.toList());
    }

    private Recommendation createDefaultRecommend(Long memberId, NewsType type) {
        Member member = memberRepositoryService.findMemberById(memberId);

        return Recommendation.builder()
                .newsType(type)
                .member(member)
                .build();
    }

    private List<Member> getFollowingMembers(Long memberId){
        List<Member> followingMembers = followRepositoryService.findFollowingByFollowedId(memberId);

        if (followingMembers.isEmpty()) {
            return List.of(); // 팔로우한 멤버가 없으면 빈 리스트 반환
        }
        return followingMembers;
    }

    private String getHistoryImageUrlByHashtagName(String hashtagName) {
        Long historyId = hashtagHistoryRepositoryService.findHistoryIdByHashtagName(hashtagName);
        if (historyId == null) {
            return null;
        } else {
            List<HistoryImage> images = historyImageRepositoryService.findByHistoryId(historyId);
            String imageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();
            if (imageUrl == null) {
                return null;
            } else {
                return imageUrl;
            }
        }
    }
}
