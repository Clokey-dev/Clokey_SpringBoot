package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.ProfileConverter;
import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    @Transactional
    public MemberResponseDTO.ProfileRP updateProfile(Long userId, MemberResponseDTO.ProfileRQ request) {
        // 사용자 확인
        Member member = memberRepositoryService.findMemberById(userId);


        // 정보 업데이트
        member.setNickname(request.getNickname());
        member.setClokeyId(request.getClokeyId());
        member.setProfileImageUrl(request.getProfileImageUrl());
        member.setBio(request.getBio());

        // 저장
        Member updatedMember = memberRepositoryService.saveMember(member);

        // 응답 생성
        return ProfileConverter.toProfileRPDTO(updatedMember);
    }
}
