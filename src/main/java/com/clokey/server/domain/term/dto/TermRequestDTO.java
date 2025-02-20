package com.clokey.server.domain.term.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.domain.term.exception.annotation.InvalidTermId;

public class TermRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EssentialTermAgree
    @InvalidTermId

    public static class Join {

        @Valid
        private List<Term> terms;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Term {
            @NotNull(message = "약관 ID는 필수 입력 값입니다.") private Long termId;
            @NotNull(message = "약관 동의는 필수 입력 값입니다.") private Boolean agreed;
        }
    }

}
