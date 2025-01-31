package com.clokey.server.domain.term.dto;

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

}
