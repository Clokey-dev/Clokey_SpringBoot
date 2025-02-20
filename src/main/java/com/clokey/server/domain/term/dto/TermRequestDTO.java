package com.clokey.server.domain.term.dto;

import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.domain.term.exception.annotation.InvalidTermId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

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
            @NotNull(message = "약관 ID는 필수 입력 값입니다.")
            private Long termId;
            @NotNull(message = "약관 동의는 필수 입력 값입니다.")
            private Boolean agreed;
        }
    }

}
