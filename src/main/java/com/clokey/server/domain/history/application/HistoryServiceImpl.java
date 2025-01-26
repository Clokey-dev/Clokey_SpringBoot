package com.clokey.server.domain.history.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.history.domain.entity.*;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryAlreadyExistValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

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
    public HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked) {

        History history = historyRepositoryService.findById(historyId);

        if(isLiked) {
            historyRepositoryService.decrementLikes(historyId);
            memberLikeRepositoryService.deleteByMember_IdAndHistory_Id(memberId,historyId);
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
    public HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content) {

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

    @Override
    public HistoryResponseDTO.DayViewResult getDaily(Long historyId, Long memberId){
        History history = historyRepositoryService.findById(historyId);
        List<HistoryImage> historyImages = historyImageRepositoryService.findByHistory_Id(historyId);
        List<String> imageUrl = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepositoryService.findByHistory_Id(historyId);
        List<String> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .map(Hashtag::getName)
                .toList();
        int likeCount = memberLikeRepositoryService.countByHistory_Id(historyId);
        boolean isLiked = memberLikeRepositoryService.existsByMember_IdAndHistory_Id(memberId,historyId);

        return HistoryConverter.toDayViewResult(history, imageUrl, hashtags, likeCount, isLiked);
    }

    @Override
    public HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page) {
        Page<Comment> comments = commentRepositoryService.findByHistoryIdAndCommentIsNull(historyId, PageRequest.of(page,10, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<List<Comment>> repliesForEachComment = comments.stream()
                .map(comment -> commentRepositoryService.findByCommentId(comment.getId()))
                .toList();
        return  HistoryConverter.toHistoryCommentResult(comments, repliesForEachComment);
    }

    @Override
    public HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long this_member_id, Long memberId, String month){
        List<History> histories = historyRepositoryService.findHistoriesByMemberAndYearMonth(memberId, month);
        List<String> historyImageUrls = histories.stream()
                .map(history -> historyImageRepositoryService.findByHistory_Id(history.getId())
                        .stream()
                        .sorted(Comparator.comparing(HistoryImage::getCreatedAt)) // createdAt 기준으로 정렬
                        .findFirst() // 첫 번째 이미지 가져오기
                        .map(HistoryImage::getImageUrl) // 이미지 URL을 추출
                        .orElse("")) // 없으면 빈 문자열 반환
                .toList();

        //나의 기록 열람은 공개 범위와 상관없이 모두 열람 가능합니다.

        if (this_member_id.equals(memberId)) {
            return HistoryConverter.toAllMonthViewResult(memberId, histories, historyImageUrls);
        }

        //다른 멤버 기록 열람시 PUBLIC 기록만을 모아줍니다.
        return HistoryConverter.toPublicMonthViewResult(memberId, histories, historyImageUrls);

    }

    @Override
    @Transactional
    public HistoryResponseDTO.HistoryCreateResult createHistory(HistoryRequestDTO.HistoryCreate historyCreateRequest,Long memberId, List<MultipartFile> imageFiles) {

        //이미 해당 날짜에 기록이 존재하는지 검증합니다.
        historyAlreadyExistValidator.validate(memberId,historyCreateRequest.getDate());

        //모든 옷이 나의 옷이 맞는지 검증합니다.
        clothAccessibleValidator.validateClothOfMember(historyCreateRequest.getClothes(),memberId);

        // History 엔티티 생성 후 요청 정보 반환해서 저장
        History history = historyRepositoryService.save(HistoryConverter.toHistory(historyCreateRequest, memberRepositoryService.findMemberById(memberId)));

        // 이미지는 첨부했다면 업로드를 진행합니다.
        if (imageFiles != null && !imageFiles.isEmpty()){
           historyImageRepositoryService.save(imageFiles,history);
        }


        //기록-옷 테이블에 추가해줍니다.
        historyCreateRequest.getClothes()
                .forEach(clothId-> {
                    historyClothRepositoryService.save(history,clothRepositoryService.findById(clothId));
                });




        historyCreateRequest.getHashtags()
                .forEach(hashtagNames -> {
                    //존재하는 해시태그라면 매핑 테이블에 추가
                    //아니라면 새로운 해시태그를 만들고 매핑 테이블에 추가
                    if(hashtagRepositoryService.existByName(hashtagNames)){
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

        return HistoryConverter.historyCreateResult(history);
    }

    @Override
    @Transactional
    public void updateHistory(HistoryRequestDTO.HistoryUpdate historyUpdate, Long memberId, Long historyId, List<MultipartFile> images) {

        //나의 기록이 맞는지 검증합니다.
        historyAccessibleValidator.validateMyHistory(historyId,memberId);

        //모든 옷이 나의 옷이 맞는지 검증합니다.
        clothAccessibleValidator.validateClothOfMember(historyUpdate.getClothes(),memberId);

        historyImageRepositoryService.deleteAllByHistory_Id(historyId);
        if(images != null && !images.isEmpty()){
            historyImageRepositoryService.save(images,historyRepositoryService.findById(historyId));
        }

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
        historyToUpdate.updateHistory(historyUpdate.getContent(),historyUpdate.getVisibility());
    }

    private void updateHistoryClothes(List<Long> updatedClothes, List<Long> savedClothes, History history) {

        //updateClothes에만 존재하는 것은 추가 대상
        List<Cloth> clothesToAdd = updatedClothes.stream()
                .filter(clothId -> !savedClothes.contains(clothId))
                .map(clothRepositoryService::findById)
                .toList();

        //반대는 삭제 대상
        List<Cloth> clothesToDelete = savedClothes.stream()
                .filter(clothId -> !updatedClothes.contains(clothId))
                .map(clothRepositoryService::findById)
                .toList();

        clothesToAdd.forEach(cloth->historyClothRepositoryService.save(history,cloth));
        clothesToDelete.forEach(cloth->historyClothRepositoryService.delete(history,cloth));
    }

    private void updateHistoryHashtags(List<String> updatedHashtags, List<String> savedHashtags, History history){

        //존재하지 않는 해시태그는 만들어줍니다.
        updatedHashtags.forEach(hashtagName->{
            if(!hashtagRepositoryService.existByName(hashtagName)) {
                Hashtag newHashtag = Hashtag.builder()
                        .name(hashtagName)
                        .build();
                hashtagRepositoryService.save(newHashtag);
            }
        });


        //updateHashtag에만 존재하는 것은 매핑 테이블에
        List<Hashtag> hashtagToAdd = updatedHashtags.stream()
                .filter(hashtagNames -> !savedHashtags.contains(hashtagNames))
                .map(hashtagRepositoryService::findByName)
                .toList();

        //반대는 삭제 대상
        List<Hashtag> hashtagToDelete = savedHashtags.stream()
                .filter(hashtagNames -> !updatedHashtags.contains(hashtagNames))
                .map(hashtagRepositoryService::findByName)
                .toList();

        hashtagToAdd.forEach(hashtag -> hashtagHistoryRepositoryService.addHashtagHistory(hashtag,history));
        hashtagToDelete.forEach(hashtag -> hashtagHistoryRepositoryService.deleteHashtagHistory(hashtag,history));
    }


}
