package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.history.domain.entity.*;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.exception.HistoryException;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryAlreadyExistValidator;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.FollowRepositoryService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final FollowRepositoryService followRepositoryService;
    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryAlreadyExistValidator historyAlreadyExistValidator;
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

    @Override
    @Transactional
    public HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked) {

        historyLikedValidator.validateIsLiked(historyId, memberId, isLiked);

        History history = historyRepositoryService.findById(historyId);

        if (isLiked) {
            historyRepositoryService.decrementLikes(historyId);
            memberLikeRepositoryService.deleteByMember_IdAndHistory_Id(memberId, historyId);
        } else {
            historyRepositoryService.incrementLikes(historyId);
            MemberLike memberLike = MemberLike.builder()
                    .history(history)
                    .member(memberRepositoryService.findMemberById(memberId))
                    .build();
            memberLikeRepositoryService.save(memberLike);
        }

        return HistoryConverter.toLikeResult(history, isLiked);
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

    private void validateParentCommentHistory(Long historyId,Long parentCommentId) {
        if(parentCommentId == null) {
            return;
        }

        Long parentHistoryId = commentRepositoryService.findById(parentCommentId).getHistory().getId();

        if(!parentHistoryId.equals(historyId)) {
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
        List<Cloth> cloths = historyClothRepositoryService.findAllClothByHistoryId(historyId);

        return HistoryConverter.toDayViewResult(history, imageUrl, hashtags, likeCount, isLiked,cloths);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page) {
        Page<Comment> comments = commentRepositoryService.findByHistoryIdAndCommentIsNull(historyId, PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<List<Comment>> repliesForEachComment = comments.stream()
                .map(comment -> commentRepositoryService.findByCommentId(comment.getId()))
                .toList();
        return HistoryConverter.toHistoryCommentResult(comments, repliesForEachComment);
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long myMemberId, String clokeyId, String month) {



        //Clokey ID를 제공하지 않았다면 자기 자신의 기록 확인으로 전부 반환.
        if(clokeyId == null){
            List<History> histories = historyRepositoryService.findHistoriesByMemberAndYearMonth(myMemberId,month);
            List<String> firstImageUrlsOfHistory = histories.stream()
                    .map(history -> historyImageRepositoryService.findByHistoryId(history.getId())
                            .stream()
                            .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                            .findFirst()
                            .map(HistoryImage::getImageUrl)
                            .orElse("")) // 사진이 없다면 빈칸
                    .collect(Collectors.toList());
            return HistoryConverter.toMonthViewResult(myMemberId, histories, firstImageUrlsOfHistory);
        }

        Member member = memberRepositoryService.findMemberByClokeyId(clokeyId);
        Long memberId = member.getId();

        //나의 기록이 아닌 경우 대상 멤버에게 접근 권한이 있는지 확인합니다.
        historyAccessibleValidator.validateMemberAccessOfMember(memberId,myMemberId);

        List<History> histories = historyRepositoryService.findHistoriesByMemberAndYearMonth(memberId,month);
        List<String> firstImageUrlsOfHistory = histories.stream()
                .map(history -> historyImageRepositoryService.findByHistoryId(history.getId())
                        .stream()
                        .sorted(Comparator.comparing(HistoryImage::getCreatedAt))
                        .findFirst()
                        .map(HistoryImage::getImageUrl)
                        .orElse("")) // 사진이 없다면 빈칸
                .collect(Collectors.toList());

        //비공개 게시물을 가려줍니다.
        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);

            if (history.getVisibility().equals(Visibility.PRIVATE)){
                firstImageUrlsOfHistory.set(i, "비공개입니다");
            }

        }
        return HistoryConverter.toMonthViewResult(memberId,histories,firstImageUrlsOfHistory);
    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreate historyCreateRequest, Long memberId, List<MultipartFile> imageFiles) {

        //이미 해당 날짜에 기록이 존재하는지 검증합니다.
        historyAlreadyExistValidator.validate(memberId, historyCreateRequest.getDate());

        //모든 옷이 나의 옷이 맞는지 검증합니다.
        clothAccessibleValidator.validateClothOfMember(historyCreateRequest.getClothes(), memberId);

        // History 엔티티 생성 후 요청 정보 반환해서 저장
        History history = historyRepositoryService.save(HistoryConverter.toHistory(historyCreateRequest, memberRepositoryService.findMemberById(memberId)));

        //이미지는 반드시 첨부해야 합니다.
        if (imageFiles == null || imageFiles.isEmpty()) {
            throw new HistoryException(ErrorStatus.MUST_POST_HISTORY_IMAGE);
        }

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

        return HistoryConverter.toHistoryCreateResult(history);
    }

    @Override
    @Transactional
    public void updateHistory(HistoryRequestDTO.HistoryUpdate historyUpdate, Long memberId, Long historyId, List<MultipartFile> images) {

        //나의 기록이 맞는지 검증합니다.
        historyAccessibleValidator.validateMyHistory(historyId, memberId);

        //모든 옷이 나의 옷이 맞는지 검증합니다.
        clothAccessibleValidator.validateClothOfMember(historyUpdate.getClothes(), memberId);

        historyImageRepositoryService.deleteAllByHistoryId(historyId);

        //이미지는 반드시 첨부해야 합니다.
        if (images == null || images.isEmpty()) {
            throw new HistoryException(ErrorStatus.MUST_POST_HISTORY_IMAGE);
        }

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
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        validateMyComment(commentId,memberId);
        commentRepositoryService.deleteChildren(commentId);
        commentRepositoryService.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(HistoryRequestDTO.UpdateComment updateCommentRequest,Long commentId, Long memberId) {
        validateMyComment(commentId,memberId);
        Comment commentToUpdate = commentRepositoryService.findById(commentId);
        commentToUpdate.updateContent(updateCommentRequest.getContent());
    }

    @Override
    @Transactional
    public void deleteHistory(Long historyId, Long memberId) {
        historyAccessibleValidator.validateMyHistory(historyId,memberId);

        //댓글 지우기
        commentRepositoryService.deleteAllComments(historyId);

        //기록_옷 지우기
        List<Cloth> cloths =historyClothRepositoryService.findAllClothByHistoryId(historyId);
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
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.LikedUserResults getLikedUser(Long memberId, Long historyId) {

        historyAccessibleValidator.validateHistoryAccessOfMember(historyId,memberId);

        List<Member> likedMembers = memberLikeRepositoryService.findMembersByHistory(historyId);
        List<Boolean> followStatus = followRepositoryService.checkFollowingStatus(memberId,likedMembers);

        return HistoryConverter.toLikedUserResult(likedMembers,followStatus);
    }

    private void validateMyComment(Long commentId, Long memberId) {
        Comment comment = commentRepositoryService.findById(commentId);
        if(!comment.getMember().getId().equals(memberId)){
            throw new HistoryException(ErrorStatus.NOT_MY_COMMENT);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryResponseDTO.LastYearHistoryResult getLastYearHistory(Long memberId) {

        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1);

        if(historyRepositoryService.checkHistoryExistOfDate(oneYearAgo,memberId)){
            Long historyOneYearAgoId = historyRepositoryService.getHistoryOfDate(oneYearAgo,memberId).getId();
            List<String> historyUrls = historyImageRepositoryService.findByHistoryId(historyOneYearAgoId).stream()
                    .map(HistoryImage::getImageUrl)
                    .toList();
            return HistoryConverter.toLastYearHistoryResult(historyOneYearAgoId,historyUrls,memberRepositoryService.findMemberById(memberId));
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
            return HistoryConverter.toLastYearHistoryResult(historyOneYearAgoId,historyUrls,memberRepositoryService.findMemberById(memberPicked));
        }

        return null;
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

        //존재하지 않는 해시태그는 만들어줍니다.
        updatedHashtags.forEach(hashtagName -> {
            if (!hashtagRepositoryService.existByName(hashtagName)) {
                Hashtag newHashtag = Hashtag.builder()
                        .name(hashtagName)
                        .build();
                hashtagRepositoryService.save(newHashtag);
            }
        });


        //updateHashtag에만 존재하는 것은 매핑 테이블에
        List<Hashtag> hashtagToAdd = hashtagRepositoryService.findHashtagsByNames(updatedHashtags.stream()
                .filter(hashtagNames -> !savedHashtags.contains(hashtagNames))
                .toList());

        //반대는 삭제 대상
        List<Hashtag> hashtagToDelete = hashtagRepositoryService.findHashtagsByNames(savedHashtags.stream()
                .filter(hashtagNames -> !updatedHashtags.contains(hashtagNames))
                .toList());

        hashtagToAdd.forEach(hashtag -> hashtagHistoryRepositoryService.addHashtagHistory(hashtag, history));
        hashtagToDelete.forEach(hashtag -> hashtagHistoryRepositoryService.deleteHashtagHistory(hashtag, history));
    }




}
