package com.clokey.server.domain.history.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.domain.entity.*;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.history.exception.HistoryException;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.FollowRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.search.application.SearchRepositoryService;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final FollowRepositoryService followRepositoryService;
    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryRepositoryService historyRepositoryService;
    private final CommentRepositoryService commentRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final ClothRepositoryService clothRepositoryService;
    private final HashtagRepositoryService hashtagRepositoryService;
    private final ClothAccessibleValidator clothAccessibleValidator;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    private final HistoryAccessibleValidator historyAccessibleValidator;
    private final SearchRepositoryService searchRepositoryService;

    @Override
    @Transactional
    public HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked) {

        historyLikedValidator.validateIsLiked(historyId, memberId, isLiked);

        if (isLiked) {
            historyRepositoryService.decrementLikes(historyId);
            memberLikeRepositoryService.deleteByMember_IdAndHistory_Id(memberId, historyId);
        } else {
            historyRepositoryService.incrementLikes(historyId);
            MemberLike memberLike = MemberLike.builder()
                    .history(historyRepositoryService.findById(historyId))
                    .member(memberRepositoryService.findMemberById(memberId))
                    .build();
            memberLikeRepositoryService.save(memberLike);
        }
        History updatedHistory = historyRepositoryService.findById(historyId);

        return HistoryConverter.toLikeResult(updatedHistory, isLiked);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content) {

        validateParentCommentHistory(historyId, parentCommentId);

        History history = historyRepositoryService.findById(historyId);

        Member member = memberRepositoryService.findMemberById(memberId);

        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepositoryService.findById(parentCommentId);
        }

        Comment comment = Comment.builder()
                .content(content)
                .comment(parentComment)
                .history(history)
                .member(member)
                .build();

        Comment savedComment = commentRepositoryService.save(comment);

        return HistoryConverter.toCommentWriteResult(savedComment);
    }

    private void validateParentCommentHistory(Long historyId, Long parentCommentId) {
        if (parentCommentId == null) {
            return;
        }

        Long parentHistoryId = commentRepositoryService.findById(parentCommentId).getHistory().getId();

        if (!parentHistoryId.equals(historyId)) {
            throw new GeneralException(ErrorStatus.PARENT_COMMENT_HISTORY_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.DailyHistoryResult getDaily(Long historyId, Long memberId) {
        historyAccessibleValidator.validateHistoryAccessOfMember(historyId, memberId);

        History history = historyRepositoryService.findById(historyId);
        List<String> imageUrl = historyImageRepositoryService.findByHistoryId(historyId).stream()
                .map(HistoryImage::getImageUrl)
                .toList();
        List<String> hashtags = hashtagHistoryRepositoryService.findHashtagNamesByHistoryId(historyId);
        int likeCount = memberLikeRepositoryService.countByHistory_Id(historyId);
        boolean isLiked = memberLikeRepositoryService.existsByMember_IdAndHistory_Id(memberId, historyId);
        Long commentCount = commentRepositoryService.countByHistoryId(historyId);
        List<Cloth> cloths = historyClothRepositoryService.findAllClothByHistoryId(historyId);

        if (memberId.equals(history.getMember().getId())) {
            return HistoryConverter.toDayViewResult(history, imageUrl, hashtags, likeCount, isLiked, cloths, commentCount);
        } else {
            cloths = cloths.stream()
                    .filter(cloth -> cloth.getVisibility() == Visibility.PUBLIC)
                    .collect(Collectors.toList());
            return HistoryConverter.toDayViewResult(history, imageUrl, hashtags, likeCount, isLiked, cloths, commentCount);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page) {
        Page<Comment> comments = commentRepositoryService.findByHistoryIdAndCommentIsNull(historyId, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<List<Comment>> repliesForEachComment = comments.stream()
                .map(comment -> commentRepositoryService.findByCommentId(comment.getId()).stream()
                        .sorted(Comparator.comparing(Comment::getCreatedAt).reversed()) // 최신 순 정렬
                        .toList()
                )
                .toList();
        return HistoryConverter.toHistoryCommentResult(comments, repliesForEachComment);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long myMemberId, String clokeyId, String month) {

        //Clokey ID를 제공하지 않았다면 자기 자신의 기록 확인으로 전부 반환.
        if (clokeyId == null) {
            List<History> histories = historyRepositoryService.findHistoriesByMemberAndYearMonth(myMemberId, month);


            List<String> firstImageUrlsOfHistory = histories.stream()
                    .map(history -> {
                        Optional<String> firstImageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                                .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                                .map(HistoryImage::getImageUrl)
                                .findFirst();

                        return firstImageUrl.orElse(null);
                    })
                    .collect(Collectors.toList());
            String nickName = memberRepositoryService.findMemberById(myMemberId).getNickname();
            return HistoryConverter.toMonthViewResult(myMemberId, nickName, histories, firstImageUrlsOfHistory);
        }

        Member member = memberRepositoryService.findMemberByClokeyId(clokeyId);
        Long memberId = member.getId();

        historyAccessibleValidator.validateMemberAccessOfMember(memberId, myMemberId);

        List<History> histories = historyRepositoryService.findHistoriesByMemberAndYearMonth(memberId, month);
        List<String> firstImageUrlsOfHistory = histories.stream()
                .map(history -> {
                    Optional<String> firstImageUrl = historyImageRepositoryService.findByHistoryId(history.getId()).stream()
                            .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                            .map(HistoryImage::getImageUrl)
                            .findFirst();

                    return firstImageUrl.orElse(null);
                })
                .collect(Collectors.toList());

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);

            if (history.getVisibility().equals(Visibility.PRIVATE)) {
                firstImageUrlsOfHistory.set(i, "비공개입니다");
            }

        }
        return HistoryConverter.toMonthViewResult(memberId, member.getNickname(), histories, firstImageUrlsOfHistory);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreate historyCreateRequest, Long memberId, List<MultipartFile> imageFiles) {

        //모든 옷이 나의 옷이 맞는지 검증합니다.
        clothAccessibleValidator.validateClothOfMember(historyCreateRequest.getClothes(), memberId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean historyExist = historyRepositoryService.checkHistoryExistOfDate(LocalDate.parse(historyCreateRequest.getDate(), formatter), memberId);

        if (historyExist) {
            return updateHistory(historyCreateRequest, memberId, historyRepositoryService.getHistoryOfDate(LocalDate.parse(historyCreateRequest.getDate()), memberId).getId(), imageFiles);
        } else {

            History history = historyRepositoryService.save(HistoryConverter.toHistory(historyCreateRequest, memberRepositoryService.findMemberById(memberId)));
            historyImageRepositoryService.save(imageFiles, history);


            List<Cloth> cloths = clothRepositoryService.findAllById(historyCreateRequest.getClothes());
            List<HistoryCloth> historyCloths = cloths.stream()
                    .map(cloth -> {
                        cloth.increaseWearNum();
                        return HistoryCloth.builder()
                                .history(history)
                                .cloth(cloth)
                                .build();
                    }).toList();
            historyClothRepositoryService.saveAll(historyCloths);

            historyCreateRequest.getHashtags()
                    .forEach(hashtagNames -> {
                        //존재하는 해시태그라면 매핑 테이블에 추가
                        //아니라면 새로운 해시태그를 만들고 매핑 테이블에 추가
                        if (hashtagRepositoryService.existByName(hashtagNames)) {
                            hashtagHistoryRepositoryService.save(HashtagHistory.builder()
                                    .history(history)
                                    .hashtag(hashtagRepositoryService.findByName(hashtagNames))
                                    .build()
                            );
                        } else {
                            Hashtag newHashtag = Hashtag.builder()
                                    .name(hashtagNames)
                                    .build();
                            hashtagRepositoryService.save(newHashtag);

                            hashtagHistoryRepositoryService.save(HashtagHistory.builder()
                                    .history(history)
                                    .hashtag(newHashtag)
                                    .build()
                            );
                        }
                    });

            // ES 동기화
            try {
                searchRepositoryService.updateHistoryDataToElasticsearch(history);
            } catch (IOException e) {
                throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
            }

            return HistoryConverter.toHistoryCreateResult(history);
        }
    }

    private HistoryResponseDTO.HistoryCreateResult updateHistory(HistoryRequestDTO.HistoryCreate historyUpdate, Long memberId, Long historyId, List<MultipartFile> images) {

        historyAccessibleValidator.validateMyHistory(historyId, memberId);

        historyImageRepositoryService.deleteAllByHistoryId(historyId);

        historyImageRepositoryService.save(images, historyRepositoryService.findById(historyId));

        updateHistoryClothes(
                historyUpdate.getClothes(),
                historyClothRepositoryService.findClothIdsByHistoryId(historyId),
                historyRepositoryService.findById(historyId));

        updateHistoryHashtags(
                historyUpdate.getHashtags(),
                hashtagHistoryRepositoryService.findByHistory_Id(historyId).stream()
                        .map(hashtagHistory -> hashtagHistory.getHashtag().getName())
                        .toList(),
                historyRepositoryService.findById(historyId));

        History historyToUpdate = historyRepositoryService.findById(historyId);
        historyToUpdate.updateHistory(historyUpdate.getContent(), historyUpdate.getVisibility());

        // ES 동기화
        try {
            searchRepositoryService.updateHistoryDataToElasticsearch(historyToUpdate);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }

        return HistoryConverter.toHistoryCreateResult(historyRepositoryService.findById(historyId));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        validateMyComment(commentId, memberId);
        commentRepositoryService.deleteChildren(commentId);
        commentRepositoryService.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(HistoryRequestDTO.UpdateComment updateCommentRequest, Long commentId, Long memberId) {
        validateMyComment(commentId, memberId);
        Comment commentToUpdate = commentRepositoryService.findById(commentId);
        commentToUpdate.updateContent(updateCommentRequest.getContent());
    }

    @Override
    @Transactional
    public void deleteHistory(Long historyId, Long memberId) {
        historyAccessibleValidator.validateMyHistory(historyId, memberId);

        commentRepositoryService.deleteAllComments(historyId);

        //기록_옷 지우기
        List<Cloth> cloths = historyClothRepositoryService.findAllClothByHistoryId(historyId);
        cloths.forEach(Cloth::decreaseWearNum);
        historyClothRepositoryService.deleteAllByHistoryId(historyId);

        //기록-해시태그 지우기
        hashtagHistoryRepositoryService.deleteAllByHistoryId(historyId);

        //좋아요 기록 삭제
        memberLikeRepositoryService.deleteAllByHistoryId(historyId);

        //기록 사진 삭제
        historyImageRepositoryService.deleteAllByHistoryId(historyId);

        //기록 삭제
        historyRepositoryService.deleteById(historyId);

        // ES 삭제
        try {
            searchRepositoryService.deleteHistoryByIdFromElasticsearch(historyId);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_DELETE_FAULT);
        }
    }

    @Override
    public HistoryResponseDTO.CheckMyHistoryResult checkIfHistoryIsMine(Long historyId, Long memberId) {

        History history = historyRepositoryService.findById(historyId);

        return HistoryResponseDTO.CheckMyHistoryResult.builder()
                .isMyHistory(history.getMember().getId().equals(memberId))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.LikedUserResults getLikedUser(Long memberId, Long historyId) {

        historyAccessibleValidator.validateHistoryAccessOfMember(historyId, memberId);

        List<Member> likedMembers = memberLikeRepositoryService.findMembersByHistory(historyId);
        List<Boolean> followStatus = followRepositoryService.checkFollowingStatus(memberId, likedMembers);
        List<Boolean> isMySelf = likedMembers.stream()
                .map(Member::getId)
                .map(likedMemberId -> likedMemberId.equals(memberId))
                .toList();

        return HistoryConverter.toLikedUserResult(likedMembers, followStatus,isMySelf);
    }

    private void validateMyComment(Long commentId, Long memberId) {
        Comment comment = commentRepositoryService.findById(commentId);
        if (!comment.getMember().getId().equals(memberId)) {
            throw new HistoryException(ErrorStatus.NOT_MY_COMMENT);
        }
    }

    private void updateHistoryClothes(List<Long> updatedClothes, List<Long> savedClothes, History history) {

        //updateClothes에만 존재하는 것은 추가 대상
        List<Cloth> clothesToAdd = clothRepositoryService.findAllById(
                updatedClothes.stream()
                        .filter(clothId -> !savedClothes.contains(clothId))
                        .toList());

        //반대는 삭제 대상
        List<Cloth> clothesToDelete = clothRepositoryService.findAllById(savedClothes.stream()
                .filter(clothId -> !updatedClothes.contains(clothId))
                .toList());

        clothesToAdd.forEach(cloth -> historyClothRepositoryService.save(history, cloth));
        clothesToDelete.forEach(cloth -> historyClothRepositoryService.delete(history, cloth));
    }

    private void updateHistoryHashtags(List<String> updatedHashtags, List<String> savedHashtags, History history) {

        //존재하지 않는 해시태그는 만들어준다.
        updatedHashtags.forEach(hashtagName -> {
            if (!hashtagRepositoryService.existByName(hashtagName)) {
                Hashtag newHashtag = Hashtag.builder()
                        .name(hashtagName)
                        .build();
                hashtagRepositoryService.save(newHashtag);
            }
        });


        List<Hashtag> hashtagToAdd = hashtagRepositoryService.findHashtagsByNames(updatedHashtags.stream()
                .filter(hashtagNames -> !savedHashtags.contains(hashtagNames))
                .toList());

        List<Hashtag> hashtagToDelete = hashtagRepositoryService.findHashtagsByNames(savedHashtags.stream()
                .filter(hashtagNames -> !updatedHashtags.contains(hashtagNames))
                .toList());

        hashtagToAdd.forEach(hashtag -> hashtagHistoryRepositoryService.addHashtagHistory(hashtag, history));
        hashtagToDelete.forEach(hashtag -> hashtagHistoryRepositoryService.deleteHashtagHistory(hashtag, history));
    }


}
