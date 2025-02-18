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

@Service
@RequiredArgsConstructor
public class FollowingServiceImpl implements FollowingService {

    private final MemberRepositoryService memberRepositoryService;
    private final FollowRepositoryService followRepositoryService;

    @Override
    public MemberDTO.GetFollowMemberResult getFollowPeople(String clokeyId, Integer page, Boolean isFollow) {
        // clokeyId로 계정 공개 여부 가져오기
        Member member = memberRepositoryService.findByClokeyId(clokeyId);

        Pageable pageable = PageRequest.of(page-1, 10);
        if(member.getVisibility()== Visibility.PUBLIC){
            if(isFollow){
                // 팔로잉 리스트 가져오기
                return GetUserConverter.toGetFollowPeopleResultDTO(followRepositoryService.findFollowingByFollowedId(member.getId(), pageable), pageable);
            }else{
                // 팔로워 리스트 가져오기
                return GetUserConverter.toGetFollowPeopleResultDTO(followRepositoryService.findFollowedByFollowingId(member.getId(), pageable), pageable);
            }
        }
        return GetUserConverter.toGetFollowPeopleResultDTO(new ArrayList<>(), pageable);
    }
}
