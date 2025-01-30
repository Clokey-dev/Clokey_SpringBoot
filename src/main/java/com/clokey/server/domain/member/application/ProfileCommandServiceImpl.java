package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.converter.ProfileConverter;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    @Transactional
    public MemberDTO.ProfileRP updateProfile(Long userId, MemberDTO.ProfileRQ request) {
        // 사용자 확인
        Member member = memberRepositoryService.findMemberById(userId);

        // 프로필 업데이트
        member.profileUpdate(request);

        if(member.getRegisterStatus()!=RegisterStatus.REGISTERED) {
            // 약관 동의가 완료되었으므로 회원의 등록 상태를 업데이트
            member.updateRegisterStatus(RegisterStatus.REGISTERED);
        }

        // 저장
        Member updatedMember = memberRepositoryService.saveMember(member);

        // 응답 생성
        return ProfileConverter.toProfileRPDTO(updatedMember);
    }
}
