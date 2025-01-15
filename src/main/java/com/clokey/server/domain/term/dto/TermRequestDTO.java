package com.clokey.server.domain.term.dto;

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
