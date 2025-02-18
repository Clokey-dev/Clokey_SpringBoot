package com.clokey.server.domain.recommendation.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.history.application.HashtagRepositoryService;
import com.clokey.server.domain.history.application.HistoryImageRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.application.FollowRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.NewsType;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.recommendation.converter.RecommendationConverter;
import com.clokey.server.domain.recommendation.domain.entity.Recommendation;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import com.clokey.server.domain.recommendation.exception.RecommendException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;
    private static final String REDIS_PREFIX_RECOMMEND = "re:";
    private static final String REDIS_PREFIX_CLOSET = "cl:";
    private static final String REDIS_PREFIX_CALENDAR = "ca:";
    private static final String REDIS_PREFIX_PEOPLE = "pe:";

    @Override
    public RecommendationResponseDTO.DailyClothesResult getRecommendClothes(Long memberId, Integer nowTemp, Integer minTemp, Integer maxTemp) {
        // 한 번의 쿼리로 온도 범위에 맞는 모든 옷을 가져오기
        List<Cloth> suitableClothes = clothRepositoryService.findSuitableClothes(memberId, nowTemp, minTemp, maxTemp);

        // 카테고리별로 하나씩 선택, parentCategory 번호를 이용하여 필터링
        Cloth top = findClothByCategory(suitableClothes, 1L);
        Cloth bottom = findClothByCategory(suitableClothes, 2L);
        Cloth outer = findClothByCategory(suitableClothes, 3L);

        // 각 카테고리에서 못 찾으면 "기타"에서 대체
        if (top == null) {
            top = findClothByCategory(suitableClothes, 4L);
        }
        if (bottom == null) {
            bottom = findClothByCategory(suitableClothes, 4L);
        }
        if (outer == null) {
            outer = findClothByCategory(suitableClothes, 4L);
        }

        if (top == null && bottom == null && outer == null) {
            return new RecommendationResponseDTO.DailyClothesResult(List.of());
        }

        List<RecommendationResponseDTO.DailyClothResult> recommendedClothes = Stream.of(top, bottom, outer)
                .filter(Objects::nonNull)
                .map(cloth -> RecommendationResponseDTO.DailyClothResult.builder()
                        .clothId(cloth.getId())
                        .imageUrl(cloth.getImage().getImageUrl())
                        .clothName(cloth.getName())
                        .build())
                .collect(Collectors.toList());

        return new RecommendationResponseDTO.DailyClothesResult(recommendedClothes);
    }

    private Cloth findClothByCategory(List<Cloth> clothes, Long parentCategoryId) {
        return clothes.stream()
                .filter(c -> c.getCategory().getParent() != null && c.getCategory().getParent().getId().equals(parentCategoryId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public RecommendationResponseDTO.DailyNewsAllResult<?> getNewsAll(Long memberId, String section, Integer page) {
        return mapToResponse(memberId, section, page);
    }

    @Override
    public RecommendationResponseDTO.DailyNewsResult getNews(Long memberId) {
        String cacheKeyRecommend = REDIS_PREFIX_RECOMMEND + memberId;
        String cacheKeyCloset = REDIS_PREFIX_CLOSET + memberId;
        String cacheKeyCalendar = REDIS_PREFIX_CALENDAR + memberId;
        String cacheKeyPeople = REDIS_PREFIX_PEOPLE + memberId;

        List<RecommendationResponseDTO.Recommend> recommendResult = getFromRedis(cacheKeyRecommend, RecommendationResponseDTO.Recommend.class);
        List<RecommendationResponseDTO.Closet> closetResult = getFromRedis(cacheKeyCloset, RecommendationResponseDTO.Closet.class);
        List<RecommendationResponseDTO.Calendar> calendarResult = getFromRedis(cacheKeyCalendar, RecommendationResponseDTO.Calendar.class);
        List<RecommendationResponseDTO.People> peopleResult = getFromRedis(cacheKeyPeople, RecommendationResponseDTO.People.class);

        Member member = memberRepositoryService.findMemberById(memberId);
        List<Member> followingMembers = getFollowingMembers(member.getId());

        //기존 거 가져옴 -> 없어? 만들어.
        if (recommendResult == null) {
            recommendResult = getRecommendList(member);
            saveToRedis(cacheKeyRecommend, recommendResult, Duration.ofHours(24));
        }
        if (closetResult == null) {
            closetResult = getClosetList(followingMembers);
            saveToRedis(cacheKeyCloset, closetResult, Duration.ofHours(3));
        }
        if (calendarResult == null) {
            calendarResult = getCalendarList(followingMembers);
            saveToRedis(cacheKeyCalendar, calendarResult, Duration.ofHours(3));
        }
        if (peopleResult == null) {
            peopleResult = getPeopleList(member);
            saveToRedis(cacheKeyPeople, peopleResult, Duration.ofHours(24));
        }


        return RecommendationConverter.toDailyNewsResult(recommendResult, closetResult, calendarResult, peopleResult);
    }

    // redis 저장 (String, responseDTO, duration)
    // 리스트만 저장하도록 변경
    private <T> void saveToRedis(String key, List<T> value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // redis 가져오기 (String) DTO 반환
    private <T> List<T> getFromRedis(String key, Class<T> clazz) {
        Object cachedData = redisTemplate.opsForValue().get(key);
        if (cachedData == null) {
            return null;
        }

        try {
            if (cachedData instanceof String json) {
                return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private RecommendationResponseDTO.DailyNewsAllResult<?> mapToResponse(Long memberId, String section, Integer page) {
        Member member = memberRepositoryService.findMemberById(memberId);

        List<Member> followingMembers = getFollowingMembers(member.getId());
        if ("closet".equals(section)) {
            Page<RecommendationResponseDTO.Closet> closetPage = getClosetPage(page, followingMembers);
            return RecommendationConverter.toDailyNewsAllResult(closetPage);

        } else if ("calendar".equals(section)) {
            Page<RecommendationResponseDTO.Calendar> calendarPage = getCalendarPage(page, followingMembers);
            return RecommendationConverter.toDailyNewsAllResult(calendarPage);
        }
        return null;
    }


    // 추천 소식 조회 - 시도하지 않은 스타일, 최근에 태그한 해시태그, 자주 착용한 카테고리
    private List<RecommendationResponseDTO.Recommend> getRecommendList(Member member) {

        List<RecommendationResponseDTO.Recommend> recommendList = new ArrayList<>();

        // 시도하지 않은 스타일 - 랜덤 추천 hashtagRepositoryService에서 사용자의 기록들이 가지고 있는 해시태그들을 제외한 다른 해시태그 추천
        String unusedHashtag = hashtagRepositoryService.findRandomUnusedHashtag(member.getId());
        recommendList.add(RecommendationConverter.toRecommendDTO(
                getHistoryImageUrlByHashtagName(unusedHashtag),
                member.getNickname() + "이 시도하지 않은 스타일",
                unusedHashtag
        ));

        // 최근에 태그한 해시태그 - 최근에 사용자가 기록에 태그한 해시태그 하나 반환
        String recentHashtag = hashtagHistoryRepositoryService.findLatestTaggedHashtag(member.getId());
        recommendList.add(RecommendationConverter.toRecommendDTO(
                getHistoryImageUrlByHashtagName(recentHashtag),
                member.getNickname() + "이 최근 태그한 해시태그",
                recentHashtag
        ));

        // 자주 착용한 카테고리 - 사용자가 가장 많이 착용한 카테고리 하나 반환
        String frequentCategory = clothRepositoryService.findMostWornCategory(member.getId());
        recommendList.add(RecommendationConverter.toRecommendDTO(
                getHistoryImageUrlByHashtagName(frequentCategory),
                member.getNickname() + "이 자주 착용한 카테고리",
                frequentCategory
        ));

        return recommendList;
    }

    // 팔로우 중인 옷장 업데이트 조회
    private List<RecommendationResponseDTO.Closet> getClosetList(List<Member> followingMembers) {

        // 팔로우한 멤버들의 최신 공개 옷 조회 (최신순 정렬)
        List<Cloth> clothesList = clothRepositoryService.findTop6ByMemberInAndVisibilityOrderByCreatedAtDesc(followingMembers, Visibility.PUBLIC);

        // 같은 날짜 + 같은 사용자가 올린 옷을 그룹화 (Map<Member + 날짜, List<Cloth>>)
        Map<Pair<Member, LocalDate>, List<Cloth>> groupedClothes = clothesList.stream()
                .collect(Collectors.groupingBy(
                        cloth -> Pair.of(cloth.getMember(), cloth.getCreatedAt().toLocalDate()) // 같은 사용자 + 같은 날짜 기준
                ));

        // 그룹화된 데이터를 `Closet` DTO로 변환
        return groupedClothes.entrySet().stream()
                .max(Comparator.comparing(entry -> entry.getKey().getSecond()))
                .map(entry -> RecommendationConverter.toClosetDTO(Map.of(entry.getKey(), entry.getValue())))
                .orElse(List.of());
    }

    private Page<RecommendationResponseDTO.Closet> getClosetPage(int page, List<Member> followingMembers) {
        Page<Cloth> clothesPage = clothRepositoryService.findByMemberInAndVisibilityOrderByCreatedAtDesc(
                followingMembers, Visibility.PUBLIC, PageRequest.of(page - 1, 6));

        List<RecommendationResponseDTO.Closet> closetList = RecommendationConverter.toClosetDTO(
                clothesPage.getContent().stream()
                        .collect(Collectors.groupingBy(cloth -> Pair.of(cloth.getMember(), cloth.getCreatedAt().toLocalDate())))
        );

        return new PageImpl<>(closetList, PageRequest.of(page - 1, 6), closetList.size());
    }

    // 팔로우 중인 캘린더 업데이트 조회
    private List<RecommendationResponseDTO.Calendar> getCalendarList(List<Member> followedMembers) {
        return fetchCalendarData(null, false, followedMembers);
    }

    private Page<RecommendationResponseDTO.Calendar> getCalendarPage(int page, List<Member> followingMembers) {
        Pageable pageable = PageRequest.of(page - 1, 6);
        List<RecommendationResponseDTO.Calendar> calendarList = fetchCalendarData(pageable, true, followingMembers);

        return new PageImpl<>(calendarList, PageRequest.of(page - 1, 6), calendarList.size());
    }

    private List<RecommendationResponseDTO.Calendar> fetchCalendarData(Pageable pageable, boolean isPaging, List<Member> followingMembers) {
        Page<History> historyPage;

        if (isPaging) {
            historyPage = historyRepositoryService.findByMemberInAndVisibilityOrderByHistoryDateDesc(
                    followingMembers, Visibility.PUBLIC, pageable);
        } else {
            List<History> historyList = historyRepositoryService.findTop6ByMemberInAndVisibilityOrderByHistoryDateDesc(
                    followingMembers, Visibility.PUBLIC);
            historyPage = new PageImpl<>(historyList);
        }

        List<Long> historyIds = historyPage.getContent().stream().map(History::getId).toList();
        Map<History, List<String>> historyImageMap = historyImageRepositoryService.findByHistoryIdIn(historyIds)
                .stream()
                .collect(Collectors.groupingBy(HistoryImage::getHistory, Collectors.mapping(HistoryImage::getImageUrl, Collectors.toList())));

        return RecommendationConverter.toCalendarDTO(historyPage, historyImageMap);
    }

    // Hot 계정 조회
    private List<RecommendationResponseDTO.People> getPeopleList(Member member) {
        //기록 최신 것부터 해시태그를 조회함. 해시태그 아이디를 hashtagHistoryRepository에서 찾아서 그 history의 주인들을 최대 네 명 추천해주는 로직.
        List<Long> hashtagIds = hashtagHistoryRepositoryService.findHashtagIdsByMemberIdOrderByHistoryDateDesc(member.getId());

        if (hashtagIds.isEmpty()) {
            return List.of(); // 해시태그가 없으면 빈 리스트 반환
        }

        // 해당 해시태그를 사용한 다른 사용자 찾기 (최대 10명) + 좋아요 많은 순
        List<History> recommendedMemberHistories = historyRepositoryService.findTop10MembersByHashtagIdsOrderByLikes(hashtagIds, member.getId());

        // 공개 범위 확인
        List<History> filteredHistories = recommendedMemberHistories.stream()
                .filter(history -> history.getMember().getVisibility().equals(Visibility.PUBLIC)) // 공개 계정인지 확인
                .filter(history -> history.getVisibility().equals(Visibility.PUBLIC)) // 히스토리가 공개 상태인지 확인
                .limit(4) // 최대 4명까지만 추천
                .toList();

        if (filteredHistories.isEmpty()) {
            return List.of(); // 공개 계정 또는 공개 히스토리가 없으면 빈 리스트 반환
        }

        // 히스토리 ID 리스트 추출
        List<Long> historyIds = filteredHistories.stream()
                .map(History::getId)
                .toList();

        // 각 히스토리에 대해 첫 번째 이미지를 찾음 (없을 수도 있음)
        Map<Long, String> historyImageMap = historyImageRepositoryService.findFirstImagesByHistoryIds(historyIds);

        // 중복 제거 및 최대 4명 추천
        return RecommendationConverter.toPeopleDTO(filteredHistories, historyImageMap);
    }

    private List<Member> getFollowingMembers(Long memberId){
        List<Member> followingMembers = followRepositoryService.findFollowingByFollowedId(memberId);

        if (followingMembers.isEmpty()) {
            return List.of(); // 팔로우한 멤버가 없으면 빈 리스트 반환
        }

        return followingMembers.stream()
                .filter(filteredmember -> filteredmember.getVisibility().equals(Visibility.PUBLIC))
                .limit(20)
                .toList();
    }

    private String getHistoryImageUrlByHashtagName(String hashtagName) {
        List<HashtagHistory> histories = hashtagHistoryRepositoryService.findTop5HistoriesByHashtagNameOrderByDateDesc(hashtagName);

        if(histories == null || histories.isEmpty()) {
            return null;
        }

        for (HashtagHistory history : histories) {
            if (history.getHistory() != null && history.getHistory().getVisibility() == Visibility.PUBLIC) {
                List<HistoryImage> images = historyImageRepositoryService.findByHistoryId(history.getHistory().getId());
                if (!images.isEmpty()) {
                    return images.get(0).getImageUrl();
                }
            }
        }

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public RecommendationResponseDTO.LastYearHistoryResult getLastYearHistory(Long memberId) {
        Member member = memberRepositoryService.findMemberById(memberId);
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        if(historyRepositoryService.checkHistoryExistOfDate(oneYearAgo,memberId)){
            Long historyOneYearAgoId = historyRepositoryService.getHistoryOfDate(oneYearAgo,memberId).getId();
            List<String> historyUrls = historyImageRepositoryService.findByHistoryId(historyOneYearAgoId).stream()
                    .map(HistoryImage::getImageUrl)
                    .toList();
            return RecommendationConverter.toLastYearHistoryResult(historyOneYearAgoId, historyUrls, member, true);
        }

        List<Long> followingMembers = followRepositoryService.findFollowedByFollowingId(memberId).stream()
                .map(Member::getId)
                .toList();

        List<Boolean> membersHaveHistoryOneYearAgo = historyRepositoryService.existsByHistoryDateAndMemberIds(oneYearAgo,followingMembers);

        Long memberPicked = getRandomMemberWithHistory(followingMembers,membersHaveHistoryOneYearAgo);

        if(memberPicked != null){
            Long historyOneYearAgoId = historyRepositoryService.getHistoryOfDate(oneYearAgo,memberPicked).getId();
            List<String> historyUrls = historyImageRepositoryService.findByHistoryId(historyOneYearAgoId).stream()
                    .map(HistoryImage::getImageUrl)
                    .toList();
            return RecommendationConverter.toLastYearHistoryResult(historyOneYearAgoId,historyUrls,memberRepositoryService.findMemberById(memberPicked), false);
        }

        return RecommendationConverter.toLastYearHistoryResult(null, null, member, true);
    }

    private Long getRandomMemberWithHistory(List<Long> followingMembers, List<Boolean> membersHaveHistoryOneYearAgo) {
        if (followingMembers == null || membersHaveHistoryOneYearAgo == null) {
            return null;
        }

        if (followingMembers.isEmpty() || membersHaveHistoryOneYearAgo.isEmpty()) {
            return null;
        }

        List<Long> candidates = new ArrayList<>();
        for (int i = 0; i < followingMembers.size(); i++) {
            if (Boolean.TRUE.equals(membersHaveHistoryOneYearAgo.get(i))) { // true인 경우만 추가
                candidates.add(followingMembers.get(i));
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        return candidates.get(new Random().nextInt(candidates.size()));
    }
}
