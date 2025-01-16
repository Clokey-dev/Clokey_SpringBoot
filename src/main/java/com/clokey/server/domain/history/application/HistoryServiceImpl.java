package com.clokey.server.domain.history.application;

import com.clokey.server.domain.model.entity.*;
import com.clokey.server.domain.model.entity.mapping.HashtagHistory;
import com.clokey.server.domain.model.entity.mapping.MemberLike;
import com.clokey.server.domain.model.repository.*;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MemberLikeRepository memberLikeRepository;
    private final HistoryImageRepository historyImageRepository;
    private final HashtagHistoryRepository hashtagHistoryRepository;

    @Override
    public HistoryResponseDTO.LikeResult changeLike(Long memberId, Long historyId, boolean isLiked) {
        History history = historyRepository.findById(historyId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_HISTORY));

        if(isLiked) {
            historyRepository.decrementLikes(historyId);
            memberLikeRepository.deleteByMember_IdAndHistory_Id(memberId,historyId);
        } else {
            historyRepository.incrementLikes(historyId);
            MemberLike memberLike = MemberLike.builder()
                    .history(history)
                    .member(memberRepository.findById(memberId).get())
                    .build();
            memberLikeRepository.save(memberLike);
        }

        return HistoryConverter.toLikeResult(history, isLiked);
    }

    @Override
    public HistoryResponseDTO.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content) {

        History history = historyRepository.findById(historyId).get();

        Member member = memberRepository.findById(memberId).get();

        Comment parentComment = null;
        if (parentCommentId != null) {
            parentComment = commentRepository.findById(parentCommentId).get();
        }

        Comment comment = Comment.builder()
                .content(content)
                .comment(parentComment)
                .history(history)
                .member(member)
                .build();

        Comment savedComment = commentRepository.save(comment);

        return HistoryConverter.toCommentWriteResult(savedComment);
    }

    @Override
    public HistoryResponseDTO.DayViewResult getDaily(Long historyId, Long memberId){
        Optional<History> history = historyRepository.findById(historyId);
        List<HistoryImage> historyImages = historyImageRepository.findByHistory_Id(historyId);
        List<String> imageUrl = historyImages.stream()
                .map(HistoryImage::getImageUrl)
                .toList();
        List<HashtagHistory> hashtagHistories = hashtagHistoryRepository.findByHistory_Id(historyId);
        List<String> hashtags = hashtagHistories.stream()
                .map(HashtagHistory::getHashtag)
                .map(Hashtag::getName)
                .toList();
        int likeCount = memberLikeRepository.countByHistory_Id(historyId);
        boolean isLiked = memberLikeRepository.existsByMember_IdAndHistory_Id(memberId,historyId);

        return HistoryConverter.toDayViewResult(history.get(), imageUrl, hashtags, likeCount, isLiked);
    }

    @Override
    public HistoryResponseDTO.HistoryCommentResult getComments(Long historyId, int page) {
        Page<Comment> comments = commentRepository.findByHistoryIdAndCommentIsNull(historyId, PageRequest.of(page,10, Sort.by(Sort.Direction.ASC, "createdAt")));
        List<List<Comment>> repliesForEachComment = comments.stream()
                .map(comment -> commentRepository.findByCommentId(comment.getId()))
                .toList();
        return  HistoryConverter.toHistoryCommentResult(comments, repliesForEachComment);
    }

    @Override
    public HistoryResponseDTO.MonthViewResult getMonthlyHistories(Long this_member_id, Long memberId, String month){
        List<History> histories = historyRepository.findHistoriesByMemberAndYearMonth(memberId, month);
        List<String> historyImageUrls = histories.stream()
                .map(history -> historyImageRepository.findByHistory_Id(history.getId())
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
