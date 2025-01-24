package com.clokey.server.domain.cloth.exception.validator;

import com.clokey.server.domain.cloth.exception.ClothException;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.domain.cloth.domain.repository.ClothRepository;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClothAccessibleValidator {

    private final MemberRepository memberRepository;
    private final ClothRepository clothRepository;

    public void validateClothAccessOfMember(Long clothId, Long memberId) {
        Cloth cloth = clothRepository.findById(clothId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_CLOTH));

        //접근 권한 확인 - 나의 옷이 아니고 비공개일 경우 접근 불가.
        boolean isPublic = cloth.getVisibility().equals(Visibility.PUBLIC);
        boolean isNotMyCloth = !cloth.getMemberId().equals(memberId);

        if (!isPublic && isNotMyCloth) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

    public void validateClothEditOfMember(Long clothId, Long memberId) {
        Cloth cloth = clothRepository.findById(clothId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_CLOTH));

        //수정 권한 확인 - 나의 옷이 아닌 경우에 수정 불가.
        boolean isNotMyCloth = !cloth.getMemberId().equals(memberId);

        if (isNotMyCloth) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_EDIT_CLOTH);
        }
    }

    public void validateMemberAccessOfMember(Long memberToBeQueried, Long memberRequestingQuery) {

        //접근 권한 확인 - 내 자신을 확인하는 것도 아니고 비공개인 경우.
        boolean selfQuery = memberToBeQueried.equals(memberRequestingQuery);
        boolean isPrivate = memberRepository.getReferenceById(memberToBeQueried)
                .getVisibility()
                .equals(Visibility.PRIVATE);

        if(!selfQuery && isPrivate) {
            throw new ClothException(ErrorStatus.NO_PERMISSION_TO_ACCESS_CLOTH);
        }
    }

}
