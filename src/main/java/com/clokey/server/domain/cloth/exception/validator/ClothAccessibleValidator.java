package com.clokey.server.domain.cloth.exception.validator;

import com.clokey.server.domain.cloth.application.ClothService;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClothAccessibleValidator {

    private final ClothService clothService;
    private final MemberRepositoryService memberRepositoryService;

    public void validateClothAccessOfMember(Long clothId, Long memberId) {
        Optional<Cloth> cloth = clothService.getClothById(clothId);

        //접근 권한 확인 - 나의 옷이 아니고 비공개일 경우 접근 불가.
        boolean isPrivate = cloth.get().getVisibility().equals(Visibility.PRIVATE);
        boolean isNotMyCloth = !cloth.get().getMember().getId().equals(memberId);

        if (isPrivate && isNotMyCloth) {
            throw new GeneralException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

    public void validateMemberAccessOfMember(Long memberToBeQueried, Long memberRequestingQuery) {

        //접근 권한 확인 - 내 자신을 확인하는 것도 아니고 비공개인 경우.
        boolean selfQuery = memberToBeQueried.equals(memberRequestingQuery);
        boolean isPrivate = memberRepositoryService.getMember(memberToBeQueried)
                .get()
                .getVisibility()
                .equals(Visibility.PRIVATE);

        if(!selfQuery && isPrivate) {
            throw new GeneralException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

}
