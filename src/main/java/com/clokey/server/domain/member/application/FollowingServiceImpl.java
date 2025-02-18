package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.GetUserConverter;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowingServiceImpl implements FollowingService {

    private final MemberRepositoryService memberRepositoryService;
    private final FollowRepositoryService followRepositoryService;

    @Override
    public MemberDTO.GetFollowMemberResult getFollowPeople(Long memberId, String clokeyId, Integer page, Boolean isFollow) {
        // clokeyId로 계정 공개 여부 가져오기
        Member findMember = memberRepositoryService.findByClokeyId(clokeyId);

        Pageable pageable = PageRequest.of(page-1, 10);
        if(findMember.getVisibility()== Visibility.PUBLIC){
            if(isFollow){
                // 팔로잉 리스트 가져오기
                List<Member> members = followRepositoryService.findFollowingByFollowedId(findMember.getId(), pageable);
                List<Boolean> isFollowings = followRepositoryService.checkFollowingStatus(memberId, members);
                return GetUserConverter.toGetFollowPeopleResultDTO(members, pageable, isFollowings);
            }else{
                // 팔로워 리스트 가져오기
                List<Member> members = followRepositoryService.findFollowedByFollowingId(findMember.getId(), pageable);
                List<Boolean> isFollowings = followRepositoryService.checkFollowingStatus(memberId, members);
                return GetUserConverter.toGetFollowPeopleResultDTO(members, pageable, isFollowings);
            }
        }
        return GetUserConverter.toGetFollowPeopleResultDTO(new ArrayList<>(), pageable, new ArrayList<>());
    }
}
