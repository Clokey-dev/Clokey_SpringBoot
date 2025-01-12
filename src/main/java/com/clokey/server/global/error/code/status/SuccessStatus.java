package com.clokey.server.global.error.code.status;

import com.clokey.server.global.error.code.BaseCode;
import com.clokey.server.global.error.code.ReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    _CREATED(HttpStatus.CREATED, "COMMON201", "요청 성공 및 리소스 생성됨"),

    //멤버 성공
    MEMBER_SUCCESS(HttpStatus.OK, "MEMBER_200", "성공적으로 조회되었습니다."),
    MEMBER_CREATED(HttpStatus.CREATED, "MEMBER_201", "성공적으로 생성되었습니다."),

    //옷 성공
    CLOTH_SUCCESS(HttpStatus.OK, "CLOTH_200", "성공적으로 조회되었습니다."),
    CLOTH_CREATED(HttpStatus.CREATED, "CLOTH_201", "성공적으로 생성되었습니다."),

    //카테고리 성공
    CATEGORY_SUCCESS(HttpStatus.OK, "CATEGORY_200", "성공적으로 조회되었습니다."),
    CATEGORY_CREATED(HttpStatus.CREATED, "CATEGORY_201", "성공적으로 생성되었습니다."),

    //폴더 성공
    FOLDER_SUCCESS(HttpStatus.OK, "FOLDER_200", "성공적으로 조회되었습니다."),
    FOLDER_CREATED(HttpStatus.CREATED, "FOLDER_201", "성공적으로 생성되었습니다."),

    //검색 성공
    SEARCH_SUCCESS(HttpStatus.OK, "SEARCH_200", "성공적으로 조회되었습니다."),

    //기록 성공
    HISTORY_SUCCESS(HttpStatus.OK, "CLOTH_200", "성공적으로 조회되었습니다."),
    HISTORY_CREATED(HttpStatus.CREATED, "CLOTH_201", "성공적으로 생성되었습니다."),

    //알림 성공
    NOTIFICATION_SUCCESS(HttpStatus.OK, "NOTIFICATION_200", "성공적으로 조회되었습니다."),
    NOTIFICATION_CREATED(HttpStatus.CREATED, "NOTIFICATION_200", "성공적으로 생성되었습니다."),

    //홈 성공
    HOME_SUCCESS(HttpStatus.OK, "HOME_200", "성공적으로 조회되었습니다."),

    //기타 멤버 관련 성공
    MEMBER_ACTION_SUCCESS(HttpStatus.OK, "MEMBER_ACTION_200", "멤버 관련 요소가 성공적으로 수정되었습니다."),
    MEMBER_ACTION_CREATED(HttpStatus.CREATED, "MEMBER_ACTION_201", "멤버 관련 요소가 성공적으로 생성되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDTO getReasonHttpStatus() {
        return ReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
