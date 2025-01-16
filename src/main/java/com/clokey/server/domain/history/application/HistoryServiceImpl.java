package com.clokey.server.domain.history.application;

import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.model.repository.CommentRepository;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.model.repository.HistoryRepository;
import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.model.repository.MemberRepository;
import com.clokey.server.domain.model.entity.Comment;
import com.clokey.server.domain.model.entity.History;
import com.clokey.server.domain.model.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService{

    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryRepository historyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    public void changeLike(Long memberId, Long historyId, boolean isLiked) {
        if(isLiked) {
            historyRepository.decrementLikes(historyId);
            memberLikeRepositoryService.deleteLike(memberId,historyId);
        } else {
            historyRepository.incrementLikes(historyId);
            memberLikeRepositoryService.saveLike(memberId,historyId);
        }
    }

    @Override
    public HistoryResponseDto.CommentWriteResult writeComment(Long historyId, Long parentCommentId, Long memberId, String content) {

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


}
