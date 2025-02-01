package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.cloth.exception.ClothException;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAccessibleValidator {

    private final MemberRepositoryService memberRepositoryService;

    // 유저가 옷에 대한 접근 권한이 있는지 검증 -> 비공개 옷을 조회하지 못하도록 함
    public void validateClothAccessOfMember(String ownerClokeyId, Long requesterId) {
        Member member = memberRepositoryService.findByClokeyId(ownerClokeyId);

        //접근 권한 확인 - 내가 아니고 비공개일 경우 접근 불가.
        boolean isPublic = member.getVisibility().equals(Visibility.PUBLIC);
        boolean isNotMyCloth = !member.getId().equals(requesterId);

        if (!isPublic && isNotMyCloth) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_ACCESS_USER);
        }
    }
}
