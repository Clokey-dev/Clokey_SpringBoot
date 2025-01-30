package com.clokey.server.domain.term.dto;

import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
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
    @EssentialTermAgree  // 이 애너테이션은 그대로 두고

    public static class Join {

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
