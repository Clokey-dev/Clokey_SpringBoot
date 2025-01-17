package com.clokey.server.domain.member.application;

import com.clokey.server.domain.follow.dao.FollowRepository;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.model.Member;
import com.clokey.server.domain.model.mapping.Follow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FollowCommandServiceImpl implements FollowCommandService {

    private final MemberRepositoryService memberRepositoryService;
    private final FollowRepository followRepository;

    @Override
    public MemberDTO.FollowRP followCheck(MemberDTO.FollowRQ request) {
        Long myUserId = memberRepositoryService.findMemberByClokeyId(request.getMyClokeyId()).getId();
        Long yourUserId = memberRepositoryService.findMemberByClokeyId(request.getYourClokeyId()).getId();

        boolean isFollow = followRepository.existsByFollowing_IdAndFollowed_Id(myUserId, yourUserId);

        return new MemberDTO.FollowRP(isFollow);
    }

    @Override
    @Transactional
    public void follow(MemberDTO.FollowRQ request) {
        // myClokeyId로 사용자 조회
        Long myUserId = memberRepositoryService.findMemberByClokeyId(request.getMyClokeyId()).getId();
        Long yourUserId = memberRepositoryService.findMemberByClokeyId(request.getYourClokeyId()).getId();

        // 팔로우 관계가 존재하는지 확인
        boolean isFollow = followRepository.existsByFollowing_IdAndFollowed_Id(myUserId, yourUserId);

        if (isFollow) {
            // 팔로우가 이미 존재하면 언팔로우 처리
            Follow follow = followRepository.findByFollowing_IdAndFollowed_Id(myUserId, yourUserId)
                    .orElseThrow(() -> new IllegalStateException("팔로우 관계가 존재하지 않습니다."));

            // 팔로우 삭제 (언팔로우)
            followRepository.delete(follow);
        } else {
            // 팔로우가 존재하지 않으면 팔로우 처리
            Follow follow = Follow.builder()
                    .following(memberRepositoryService.findMemberById(myUserId))
                    .followed(memberRepositoryService.findMemberById(yourUserId))
                    .build();

            // 팔로우 저장
            followRepository.save(follow);
        }
    }
    
}
