package com.clokey.server.domain.cloth.exception.validator;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.exception.ClothException;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClothAccessibleValidator {

    private final MemberRepositoryService memberRepositoryService;
    private final ClothRepositoryService clothRepositoryService;

    public void validateClothAccessOfMember(Long clothId, Long memberId) {
        Cloth cloth = clothRepositoryService.findById(clothId);

        //접근 권한 확인 - 나의 옷이 아니고 비공개일 경우 접근 불가.
        boolean isPublic = cloth.getVisibility().equals(Visibility.PUBLIC);
        boolean isNotMyCloth = !cloth.getMemberId().equals(memberId);

        if (!isPublic && isNotMyCloth) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

    public void validateClothOfMember(Long clothId, Long memberId) {
        Cloth cloth = clothRepositoryService.findById(clothId);

        //내 옷이 아닌지 확인
        boolean isNotMyCloth = !cloth.getMemberId().equals(memberId);

        if (isNotMyCloth) {
            throw new ClothException(ErrorStatus.NOT_MY_CLOTH);
        }
    }

    //다른 입력 인자로 오버로딩
    public void validateClothOfMember(List<Long> clothIds, Long memberId) {

        clothIds.forEach(clothId -> {
            validateClothOfMember(clothId,memberId);
        });
    }

    public void validateMemberAccessOfMember(Long memberToBeQueried, Long memberRequestingQuery) {

        //접근 권한 확인 - 내 자신을 확인하는 것도 아니고 비공개인 경우.
        boolean selfQuery = memberToBeQueried.equals(memberRequestingQuery);
        boolean isPrivate = memberRepositoryService.getReferencedById(memberToBeQueried)
                .getVisibility()
                .equals(Visibility.PRIVATE);

        if(!selfQuery && isPrivate) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

}
