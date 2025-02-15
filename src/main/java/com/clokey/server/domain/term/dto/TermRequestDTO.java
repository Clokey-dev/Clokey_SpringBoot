package com.clokey.server.domain.term.dto;

import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.domain.term.exception.annotation.InvalidTermId;
import jakarta.validation.Valid;
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
            private Long termId;
            private Boolean agreed;
        }
    }

}
