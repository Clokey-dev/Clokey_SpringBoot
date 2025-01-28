package com.clokey.server.domain.history.converter;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Visibility;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class HistoryConverter {

    public static HistoryResponseDTO.DailyHistoryView toDayViewResult(History history, List<String> imageUrl, List<String> hashtags, int likeCount, boolean isLiked, List<Cloth> cloths) {
        return HistoryResponseDTO.DailyHistoryView.builder()
                .userId(history.getMember().getId())
                .contents(history.getContent())
                .imageUrl(imageUrl)
                .hashtags(hashtags)
                .visibility(history.getVisibility().equals(Visibility.PUBLIC))
                .likeCount(likeCount)
                .isLiked(isLiked)
                .date(history.getHistoryDate())
                .nickName(history.getMember().getNickname())
                .clokeyId(history.getMember().getClokeyId())
                .cloths(cloths.stream()
                        .map(HistoryConverter::toHistoryCloth)
                        .toList())
                .build();
    }

    private static HistoryResponseDTO.HistoryCloth toHistoryCloth(Cloth cloth){
        return HistoryResponseDTO.HistoryCloth.builder()
                .clothId(cloth.getId())
                .clothImageUrl(cloth.getClothUrl())
                .build();
    }

    public static HistoryResponseDTO.MonthViewResult toPublicMonthViewResult(Long memberId, List<History> histories , List<String> historyFirstImageUrls) {

        List<HistoryResponseDTO.HistoryResult> HistoryResults = new ArrayList<>();

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);

            String historyImageUrl;

            //공개된 경우 사진 URL을 가져오고 아닌 경우 "비공개입니다"를 반환합니다.
            if (history.getVisibility().equals(Visibility.PUBLIC)){
                historyImageUrl = historyFirstImageUrls.get(i);
            } else {
                historyImageUrl = "비공개입니다";
            }

            HistoryResults.add(toHistoryResult(history, historyImageUrl));
        }

        return HistoryResponseDTO.MonthViewResult.builder()
                .userId(memberId)
                .histories(HistoryResults)
                .build();
    }

    public static HistoryResponseDTO.MonthViewResult toAllMonthViewResult(Long memberId, List<History> histories , List<String> historyFirstImageUrls) {

        List<HistoryResponseDTO.HistoryResult> HistoryResults = new ArrayList<>();

        for (int i = 0; i < histories.size(); i++) {
            History history = histories.get(i);
            String historyImageUrl = historyFirstImageUrls.get(i);

            HistoryResults.add(toHistoryResult(history, historyImageUrl));
        }

        return HistoryResponseDTO.MonthViewResult.builder()
                .userId(memberId)
                .histories(HistoryResults)
                .build();
    }

    private static HistoryResponseDTO.HistoryResult toHistoryResult(History history, String historyImageUrl) {
        return HistoryResponseDTO.HistoryResult.builder()
                .historyId(history.getId())
                .date(history.getHistoryDate())
                .imageUrl(historyImageUrl)
                .build();
    }


    public static HistoryResponseDTO.LikeResult toLikeResult(History history, boolean isLiked){
        return HistoryResponseDTO.LikeResult.builder()
                .historyId(history.getId())
                .isLiked(!isLiked)
                .likeCount(history.getLikes())
                .build();
    }

    public static HistoryResponseDTO.HistoryCommentResult toHistoryCommentResult(Page<Comment> comments, List<List<Comment>> replies) {
        return HistoryResponseDTO.HistoryCommentResult.builder()
                .comments(toCommentResultList(comments,replies))
                .totalPage(comments.getTotalPages())
                .totalElements(comments.getNumberOfElements())
                .isFirst(comments.isFirst())
                .isLast(comments.isLast())
                .build();
    };



    private static List<HistoryResponseDTO.CommentResult> toCommentResultList(Page<Comment> comments, List<List<Comment>> replies) {
        return IntStream.range(0, comments.getContent().size())
                .mapToObj(i -> {
                    Comment comment = comments.getContent().get(i);
                    List<Comment> replyList = replies.get(i);
                    return HistoryResponseDTO.CommentResult.builder()
                            .commentId(comment.getId())
                            .memberId(comment.getMember().getId())
                            .userImageUrl(comment.getMember().getProfileImageUrl())
                            .content(comment.getContent())
                            .replyResults(toReplyResultList(replyList))
                            .build();
                })
                .toList();
    }

    private static List<HistoryResponseDTO.ReplyResult> toReplyResultList(List<Comment> replies) {
        return replies.stream()
                .map(reply-> HistoryResponseDTO.ReplyResult.builder()
                        .commentId(reply.getId())
                        .MemberId(reply.getMember().getId())
                        .userImageUrl(reply.getMember().getProfileImageUrl())
                        .content(reply.getContent())
                        .build())
                .toList();
    }

    public static HistoryResponseDTO.CommentWriteResult toCommentWriteResult(Comment comment){
        return HistoryResponseDTO.CommentWriteResult.builder()
                .commentId(comment.getId())
                .build();
    }

    public static History toHistory(HistoryRequestDTO.HistoryCreate request, Member member) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 패턴 지정

        return History.builder()
                .historyDate(LocalDate.parse(request.getDate(),formatter))
                .likes(0)
                .visibility(request.getVisibility())
                .content(request.getContent())
                .member(member)
                .build();
    }

    public static HistoryResponseDTO.HistoryCreateResult toHistoryCreateResult(History history){
        return HistoryResponseDTO.HistoryCreateResult.builder()
                .historyId(history.getId())
                .build();
    }

    public static HistoryResponseDTO.LastYearHistoryResult toLastYearHistoryResult(Long historyId, List<String> historyImageUrls) {
        return HistoryResponseDTO.LastYearHistoryResult.builder()
                .historyId(historyId)
                .imageUrls(historyImageUrls)
                .build();
    }



}

