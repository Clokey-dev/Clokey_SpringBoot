package com.clokey.server.global.error.code.status;

import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 기본 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    //페이징 에러
    PAGE_UNDER_ONE(HttpStatus.NOT_FOUND,"PAGE_4001","페이지는 1이상이여야 합니다."),

    //멤버 에러
    NO_SUCH_TERM(HttpStatus.NOT_FOUND,"MEMBER_4041","존재하지 않는 약관ID입니다."),
    ESSENTIAL_TERM_NOT_AGREED(HttpStatus.BAD_REQUEST,"MEMBER_4002","필수 약관에 동의하지 않았습니다."),
    NO_SUCH_MEMBER(HttpStatus.NOT_FOUND,"MEMBER_4043","존재하지 않는 멤버 ID입니다."),
    CLOKEY_ID_INVALID(HttpStatus.BAD_REQUEST,"MEMBER_4004","잘못된 클로키 아이디입니다."),
    DUPLICATE_CLOKEY_ID(HttpStatus.BAD_REQUEST,"MEMBER_4005","중복된 클로키 아이디입니다."),
    ESSENTIAL_INPUT_REQUIRED(HttpStatus.BAD_REQUEST,"MEMBER_4006","필수 입력 요소 값이 누락되었습니다."),

    //옷 에러
    NO_SUCH_CLOTH(HttpStatus.NOT_FOUND,"CLOTH_4041","존재하지 않는 옷 ID입니다."),
    NO_PERMISSION_TO_ACCESS_CLOTH(HttpStatus.BAD_REQUEST,"CLOTH_4002","옷에 대한 접근 권한이 없습니다."),
    NO_PERMISSION_TO_EDIT_CLOTH(HttpStatus.BAD_REQUEST,"CLOTH_4003","옷에 대한 수정 권한이 없습니다"),
    CLOTH_VISIBILITY_INVALID(HttpStatus.BAD_REQUEST,"CLOTH_4004","잘못된 visibility 값을 입력했습니다"),
    CLOTH_TEMP_OUT_OF_RANGE(HttpStatus.BAD_REQUEST,"CLOTH_4005","옷의 상한 또는 하한 온도가 범위를 벗어났습니다"),
    CLOTH_TEMP_ORDER_INVALID(HttpStatus.BAD_REQUEST,"CLOTH_4006","옷의 하한 온도가 상한 온도보다 높습니다."),
    CLOTH_THICKNESS_OUT_OF_RANGE(HttpStatus.BAD_REQUEST,"CLOTH_4005","옷의 두께가 범위를 벗어났습니다."),

    //폴더 에러
    NO_SUCH_FOLDER(HttpStatus.NOT_FOUND,"FOLDER_4041","존재하지 않는 폴더 ID입니다."),
    FOLDER_NAME_INVALID(HttpStatus.BAD_REQUEST,"FOLDER_4002","잘못된 폴더 이름입니다."),
    NO_SUCH_CLOTH_IN_FOLDER(HttpStatus.BAD_REQUEST,"FOLDER_4003","폴더에 존재하는 옷이 아닙니다."),
    FAILED_TO_DELETE_FOLDER(HttpStatus.BAD_REQUEST,"FOLDER_4004","폴더 삭제에 실패했습니다."),

    //카테고리 에러
    NO_SUCH_CATEGORY(HttpStatus.NOT_FOUND,"CATEGORY_4041","존재하지 않는 카테고리 ID입니다."),

    //기록 에러
    DATE_INVALID(HttpStatus.BAD_REQUEST,"HISTORY_4001","잘못된 날짜 형식입니다."),
    NO_SUCH_HISTORY(HttpStatus.NOT_FOUND,"HISTORY_4002","존재하지 않는 기록 ID입니다."),
    HISTORY_VISIBILITY_INVALID(HttpStatus.BAD_REQUEST,"HISTORY_4003","잘못된 visibility 값을 입력했습니다."),
    IS_LIKED_INVALID(HttpStatus.BAD_REQUEST,"HISTORY_4004","잘못된 isLiked 값을 입력했습니다."),
    NO_SUCH_COMMENT(HttpStatus.NOT_FOUND,"HISTORY_4005","존재하지 않는 댓글 ID입니다."),
    NO_PERMISSION_TO_ACCESS_HISTORY(HttpStatus.BAD_REQUEST,"HISTORY_4006","기록에 접근 권한이 없습니다."),
    COMMENT_LENGTH_OUT_OF_RANGE(HttpStatus.BAD_REQUEST,"HISTORY_4007","댓글은 빈칸이 아닌 50자 이하여야 합니다."),
    NESTED_COMMENT(HttpStatus.BAD_REQUEST,"HISTORY_4008","대댓글에 답장을 남길 수 없습니다."),
    PARENT_COMMENT_HISTORY_ERROR(HttpStatus.BAD_REQUEST,"HISTORY_4009","부모 댓글의 기록과 작성하는 댓글의 기록이 일치하지 않습니다."),

    //알림 에러
    NOTIFICATION_TYPE_INVALID(HttpStatus.BAD_REQUEST,"NOTIFICATION_4001","잘못된 알림 Type 입니다."),

    //검색 에러
    NO_SUCH_PARAMETER(HttpStatus.BAD_REQUEST,"SEARCH_4001","검색어는 필수입니다."),

    //추천 에러
    NO_TEMP_PARAMETER(HttpStatus.BAD_REQUEST,"RECOMMEND_4001","온도값은 필수입니다."),

    //로그인 에러
    INVALID_TOKEN(HttpStatus.BAD_REQUEST,"LOGIN_4001","유효하지 않은 토큰입니다."),
    MISSING_LOGIN_TYPE(HttpStatus.BAD_REQUEST,"LOGIN_4002","로그인 타입이 누락되었습니다."),
    INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST,"LOGIN_4003","잘못된 로그인 타입입니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST,"LOGIN_4004","로그인에 실패했습니다."),

    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
