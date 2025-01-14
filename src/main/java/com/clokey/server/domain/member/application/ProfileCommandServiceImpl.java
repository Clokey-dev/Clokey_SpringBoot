package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.ProfileConverter;
import com.clokey.server.domain.member.dto.ProfileRequestDTO;
import com.clokey.server.domain.member.dto.ProfileResponseDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.Member;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    @Transactional
    public ProfileResponseDTO.ProfileRP updateProfile(Long userId, ProfileRequestDTO.ProfileRQ request) {
        // 사용자 확인
        Member member = memberRepositoryService.findMemberById(userId);

        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new MemberException(ErrorStatus.ESSENTIAL_INPUT_REQUIRED);
        }

        if (request.getClokeyId() == null || request.getClokeyId().trim().isEmpty()) {
            throw new MemberException(ErrorStatus.ESSENTIAL_INPUT_REQUIRED);
        }

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
