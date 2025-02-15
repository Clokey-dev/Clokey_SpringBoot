package com.clokey.server.domain.term.dto;

import io.swagger.v3.oas.annotations.info.Info;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermResponseDTO {

    private Long userId;
    private List<Term> terms;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Term {
        private Long termId;
        private Boolean agreed;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TermList {
        private Long termId;
        private String title;
        private String content;
        private boolean optional;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class OptionalTermDTO {
        private Long termId;   // 약관 ID
        private String title;   // 약관 제목
        private boolean agreed; // 동의 여부 (true: 동의, false: 미동의)

    }


    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserAgreementDTO {
        private String socialType;
        private String email;         // 사용자 이메일
        private String appVersion;    // 앱 버전
        private List<OptionalTermDTO> terms; // 약관 리스트
    }

}
