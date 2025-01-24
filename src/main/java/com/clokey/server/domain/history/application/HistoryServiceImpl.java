package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.entity.Hashtag;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import com.clokey.server.domain.history.domain.repository.*;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.history.domain.entity.HashtagHistory;
import com.clokey.server.domain.history.domain.entity.MemberLike;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final HistoryRepositoryService historyRepositoryService;
    private final CommentRepositoryService commentRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;

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

}
